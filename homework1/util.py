'''
This module has utility functions

@author: Hernaldo Henriquez
September 2014
'''

def partition(lst, n):
    division = len(lst) / float(n)
    return [ lst[int(round(division * i)): int(round(division * (i + 1)))] for i in range(n) ]