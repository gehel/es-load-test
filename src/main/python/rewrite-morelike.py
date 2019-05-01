#!/usr/bin/python2.7
from __future__ import print_function
import sys
import os
import json
import binascii
import pprint

stdin = os.fdopen(sys.stdin.fileno(), 'rb', 0)
stdout = os.fdopen(sys.stdout.fileno(), 'wt', 1)
debug = open("/tmp/erik", "wt", 1)

def output_line(line, reason, force_debug=False):
    print(line, file=stdout)
    if force_debug:
        print(reason, file=debug)
        print(binascii.unhexlify(line), file=debug)

morelike_seen = 0

for line in stdin:
    if not line:
        continue
    line = line.rstrip()
    decoded = binascii.unhexlify(line)

    type = decoded[0:1]
    if type != '1':
        output_line("", "not request data")
        continue

    # split into headers and content
    pos = decoded.find("\r\n\r\n")
    if pos == -1:
        output_line(line, "No split between header and content")
        continue

    header = decoded[0:pos]
    content = decoded[pos+4:]

    try:
        query = json.loads(content)
    except ValueError:
        output_line(line, "Error decoding line")
        continue

    try:
        is_more_like = query['stats'][0] == 'more_like'
    except KeyError:
        is_more_like = False

    if not is_more_like:
        output_line(line, "no stats key or not more like:\n" + pprint.pformat(query, indent=4))
        continue

    morelike_seen += 1
    if morelike_seen % 5 != 0:
        output_line("", "drop 4 in 5 more like")
        continue

    try:
        query['fields'] = 'opening_text.word_count'
        query['query']['more_like_this']['fields'] = ['opening_text']
        query['highlight']['fields']['opening_text'] = query['highlight']['fields']['text']
        del query['highlight']['fields']['text']
        del query['highlight']['fields']['file_text']
        query['highlight']['fields']['opening_text']['matched_fieds'] = ['opening_text', 'opening_text.plain']
    except:
        e = sys.exc_info()[0]
        output_line(line, "Error rewriting more like: " + str(e))
        continue

    encoded = binascii.hexlify(header + "\r\n\r\n" + json.dumps(query, separators=(',', ':')))
    output_line(encoded, "Rewrote morelike query")
