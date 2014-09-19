'''
This module has utility functions

@author: Hernaldo Henriquez
September 2014
'''

def partition(lst, n):
    division = len(lst) / float(n)
    return [ lst[int(round(division * i)): int(round(division * (i + 1)))] for i in range(n) ]

def neighbors(vector):
	return [i for i in range(len(vector)) if vector[i] == 1]

def connected_and_not_checked(vector, checked, node):
	condition = ((vector[node] == 1) and (not checked[node]))
	if condition:
		checked[node] = True
	return condition

def get_new_level(lvl, vectors, checked):
	new_lvl = []
	for node in lvl:
		# new nodes are conected to the lvl and not checked
		vector = vectors[node]
		new_lvl = new_lvl + [i for i in range(len(vector)) if connected_and_not_checked(vector, checked, i)]
	return new_lvl


def bfs_levels(source, vectors):
	# returns a list of list with the nodes that are in each bfs level
	levels = []
	checked = [False for i in range(len(vectors))]
	checked[source] = True
	first_lvl = neighbors(vectors[source])
	for node in first_lvl:
		checked[node] = True
	levels.append(first_lvl)
	new_lvl = get_new_level(levels[0], vectors, checked)
	while(new_lvl):
		levels.append(new_lvl)
		new_lvl = get_new_level(new_lvl, vectors, checked)
	return levels