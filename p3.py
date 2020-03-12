# Johnathan Alexander
# Seamus Mcfarland
from math import inf
from collections import OrderedDict
import queue
import heapq

def djikstra_shortest_paths(vertices, adj_list, weights, start):
    S = {}
    for vert in vertices:
        S[vert] = inf
    S[start] = 0
    Q = []
    # TODO: implement

    entryDict = {}
    for vert, dist in S.items():
        entry = [dist, vert]
        entryDict[vert] = entry
        heapq.heappush(Q, entry)

    while len(Q) > 0:
        currDist, currVert = heapq.heappop(Q)

        for neighbor, nDist in weights[currVert].items():
            distance = S[currVert] + nDist
            if distance < S[neighbor]:
                S[neighbor] = distance
                entryDict[neighbor][0] = distance
    return S

#####################################################################

def sortFreq(freqs):  #sort frequencies to start tree
    sort = sorted(freqs.items(), key=lambda kv: kv[1])
    flip = list((v,k) for k,v in sort)
    return flip

def huffTree(freqs):  #build huffman tree
    while len(freqs) > 1:
        twoMin = tuple(freqs[0:2])
        otherFreqs = freqs[2:]
        combine = twoMin[0][0] + twoMin[1][0]
        freqs = otherFreqs + [(combine,twoMin)]
        freqs.sort()
    return freqs[0]


def removeFreq(tree):  #removes frequencies from tree, leaving just the letters
    t = tree[1]
    if type(t) == type(''):
        return t
    else:
        return (removeFreq(t[0]), removeFreq(t[1]))


def getCodes(chars, path='', codes = {}):  #traverses tree to get codes
    if type(chars) == type(''):
        codes[chars] = path
    else:
        getCodes(chars[0], path + "0")
        getCodes(chars[1], path + "1")
    return codes

def huffman_codes(chars, freqs):
    sortedFreqs = sortFreq(freqs)
    tree = huffTree(sortedFreqs)
    trim = removeFreq(tree)
    huffcodes = getCodes(trim)
    newcodes = OrderedDict(sorted(huffcodes.items()))
    codes = {}
    for k, v in newcodes.items():
        codes[k] = v
    return codes

if __name__ == '__main__':
    vertices = [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    adj_list = {
        'a': [ 'b', 'c', 'e' ],
        'b': [ 'c', 'd' ],
        'c': [ 'd' ],
        'd': [ 'f' ],
        'e': [ 'f' ],
        'f': []
    }
    weights = {
        'a': { 'b': 2, 'c': 6, 'e': 4 },
        'b': { 'c': 3, 'd': 2 },
        'c': { 'd': 0 },
        'd': { 'f': 2 },
        'e': { 'f': 4 },
        'f': {}
    }
    results = {
        'a': { 'a': 0, 'b': 2, 'c': 5, 'd': 4, 'e': 4, 'f': 6 },
        'b': { 'a': inf, 'b': 0, 'c': 3, 'd': 2, 'e': inf, 'f': 4 },
        'c': { 'a': inf, 'b': inf, 'c': 0, 'd': 0, 'e': inf, 'f': 2 },
        'd': { 'a': inf, 'b': inf, 'c': inf, 'd': 0, 'e': inf, 'f': 2 },
        'e': { 'a': inf, 'b': inf, 'c': inf, 'd': inf, 'e': 0, 'f': 4 },
        'f': { 'a': inf, 'b': inf, 'c': inf, 'd': inf, 'e': inf, 'f': 0 }
    }
    for vert in vertices:
        paths = djikstra_shortest_paths(vertices, adj_list, weights, vert)
        for target in vertices:
            if paths[target] != results[vert][target]:
                print('expected', vert, '->', target, 'to equal', results[vert][target], 'received', paths[target])
    chars = [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    freqs = {
        'a': 18,
        'b': 22,
        'c': 15,
        'd': 13,
        'e': 20,
        'f': 12
    }
    results = {
        'a': '111', 'b': '01', 'c': '110', 'd': '101', 'e': '00', 'f': '100'
    }
    codes = huffman_codes(chars, freqs)
    for c in chars:
        if codes[c] != results[c]:
            print('expected codeword of', c, 'to be', results[c], 'received', codes[c])
    print('Done!')
