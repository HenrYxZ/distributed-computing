from mpi4py import MPI
comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()

if rank == 0:
	data = [(i+1)**2 for i in range(size)]
	print (str(data))
else:
	data = None

data = comm.scatter(data, root=0)
print (str(rank)+":"+str(data))
assert data == (rank+1)**2