# Johnathan Alexander, Ferdinand Mudjialim
# Python implementation of stable matching problem

import copy

def gs(men, women, pref):
    """
    Gale-Shapley algorithm
    Inputs: men (list of men's names)
            women (list of women's names)
            pref (dictionary of preferences mapping names to
                list of preferred names in sorted order)
    Output: the stable match S which is a
        list of tuples of the form (man, woman)
    """
    s = []
    unmatchedMen = [] + men
    prefDict = copy.deepcopy(pref)
    matchDict = {}
    m = unmatchedMen[0]  # choose one man
    # while some man m unmatched and hasn't proposed to every woman
    while unmatchedMen and prefDict[m]:
        # while some woman w on m's preflist hasn't been proposed to by m
        while prefDict[m]:
            m = unmatchedMen[0]
            w = prefDict[m][0]
            if w not in matchDict.keys():  # if w unmatched
                s.append((m, w))  # these two are now matched for now
                matchDict[m] = w
                matchDict[w] = m
                unmatchedMen.pop(0)  # m is now matched
                prefDict[m].pop(0)  # m can't propose to same woman
            elif prefDict[w].index(m) < prefDict[w].index(matchDict[w]):
                # remove m'-w from S
                oldm = matchDict[w]
                s.remove((oldm, w))  # remove current partner matching

                s.append((m, w))  # new match
                matchDict[m] = w
                matchDict[w] = m
                unmatchedMen.pop(0)
                prefDict[m].pop(0)

                unmatchedMen.insert(0, oldm)
            else:
                prefDict[m].pop(0)

    return s


def gs_block(men, women, pref, blocked):
    """
    Gale-Shapley algorithm, modified to exclude unacceptable matches
    Inputs: men (list of men's names)
            women (list of women's names)
            pref (dictionary of preferences mapping names to
                list of preferred names in sorted order)
            blocked (list of (man, woman) tuples that are unacceptable matches)
    Output: the modified stable match S which is a
        list of tuples of the form (man, woman)
    """
    s = []
    locked = []
    forbidden = []
    for i in blocked:
        if i[0] not in forbidden:
            forbidden.append(i[0])
    unmatchedMen = copy.deepcopy(forbidden)
    for j in men:
        if j not in forbidden:
            unmatchedMen.append(j)
    prefDict = copy.deepcopy(pref)
    matchDict = {}
    m = unmatchedMen[0]
    # while some man m unmatched and hasn't proposed to every woman
    while unmatchedMen and prefDict[m]:
        # m = unmatchedMen[0]
        # while some woman w on m's preflist hasn't been proposed to by m
        while prefDict[m]:
            m = unmatchedMen[0]
            w = prefDict[m][0]
            if w not in matchDict.keys():  # if w unmatched
                s.append((m, w))  # these two are now matched for now
                matchDict[m] = w
                matchDict[w] = m
                unmatchedMen.pop(0)  # m is now matched
                prefDict[m].pop(0)  # m can't propose to same woman
                if m in forbidden:
                    locked.append(m)
            elif prefDict[w].index(m) < prefDict[w].index(matchDict[w]) and (matchDict[w] not in locked):
                # remove m'-w from S
                oldm = matchDict[w]
                s.remove((oldm, w))  # remove current partner matching

                s.append((m, w))  # new match
                matchDict[m] = w
                matchDict[w] = m
                unmatchedMen.pop(0)
                prefDict[m].pop(0)

                unmatchedMen.insert(0, oldm)
            else:
                prefDict[m].pop(0)

    return s


def gs_tie(men, women, preftie):
    """
    Gale-Shapley algorithm, modified to use preferences with ties
    Inputs: men (list of men's names)
            women (list of women's names)
            preftie (dictionary of preferences mapping names to
                list of sets of preferred names in sorted order)
    Output: the stable match S which is a list of pairs of the form (m, w)
    """
    s = []
    unmatchedMen = [] + men
    prefDict = copy.deepcopy(preftie)
    matchDict = {}
    m = unmatchedMen[0]  # choose one man

    def comparePref(woman, newman, oldman):
        # tests to see if woman likes newman better
        # elif prefDict[w].index(m) < prefDict[w].index(matchDict[w]):
        # prefDict[w] = [{'a','b'}, {'c'}]
        newmanPref = None
        oldmanPref = None
        i = 0
        j = 0
        for aSet in prefDict[woman]:
            if oldman in aSet:
                oldmanPref = i
                break
            i += 1
        for aSet in prefDict[woman]:
            if newman in aSet:
                newmanPref = j
                break
            j += 1
        return newmanPref < oldmanPref

    # while some man m unmatched and hasn't proposed to every woman
    while unmatchedMen and prefDict[m]:
        m = unmatchedMen[0]
        w = prefDict[m][0].pop()  # pop first woman from set
        if prefDict[m][0] == set():  # if empty, then remove set
            del prefDict[m][0]
        if w not in matchDict.keys():  # if w unmatched
            s.append((m, w))  # these two are now matched for now
            matchDict[m] = w
            matchDict[w] = m
            unmatchedMen.pop(0)  # m is now matched
        elif comparePref(w, m, matchDict[w]):
            # remove m'-w from S
            oldm = matchDict[w]
            s.remove((oldm, w))  # remove current partner matching

            s.append((m, w))  # new match
            matchDict[m] = w
            matchDict[w] = m
            unmatchedMen.pop(0)
            unmatchedMen.insert(0, oldm)
        else:
            pass  # rejected
    return s

if __name__ == "__main__":
    # input data

    the_men = ['xavier', 'yancey', 'zeus']
    the_women = ['amy', 'bertha', 'clare']

    the_pref = {
        'xavier': ['amy', 'bertha', 'clare'],
        'yancey': ['bertha', 'amy', 'clare'],
        'zeus': ['amy', 'bertha', 'clare'],
        'amy': ['yancey', 'xavier', 'zeus'],
        'bertha': ['xavier', 'yancey', 'zeus'],
        'clare': ['xavier', 'yancey', 'zeus']
    }

    the_preftie = {
        'xavier': [{'bertha'}, {'amy'}, {'clare'}],
        'yancey': [{'amy', 'bertha'}, {'clare'}],
        'zeus': [{'amy'}, {'bertha', 'clare'}],
        'amy': [{'zeus', 'xavier', 'yancey'}],
        'bertha': [{'zeus'}, {'xavier'}, {'yancey'}],
        'clare': [{'xavier', 'yancey'}, {'zeus'}]
    }

    blocked = [
        ('xavier', 'clare'),
        ('zeus', 'clare'),
        ('zeus', 'amy')
    ]

    match = gs(the_men, the_women, the_pref)
    print(match)

    match_block = gs_block(the_men, the_women, the_pref, blocked)
    print(match_block)

    match_tie = gs_tie(the_men, the_women, the_preftie)
    print(match_tie)
