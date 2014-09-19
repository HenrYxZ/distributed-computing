import util
import numpy as np

# vectors = np.array([[0, 1, 0], [1, 0, 1], [0, 1, 0]])
node = 7

vectors = np.zeros((10,10), dtype = np.int)
# edges
vectors[0][1] = 1
vectors[1][2] = 1
vectors[1][5] = 1
vectors[2][7] = 1
vectors[3][4] = 1
# vectors[3][5] = 1
vectors[3][6] = 1
vectors[3][9] = 1
vectors[4][7] = 1
vectors[5][6] = 1
vectors[5][7] = 1
vectors[7][8] = 1

vectors[1][0] = 1
vectors[2][1] = 1
vectors[4][3] = 1
vectors[5][1] = 1
# vectors[5][3] = 1
vectors[6][3] = 1
vectors[6][5] = 1
vectors[7][2] = 1
vectors[7][4] = 1
vectors[7][5] = 1
vectors[8][7] = 1

lvls = util.bfs_levels(node, vectors)
