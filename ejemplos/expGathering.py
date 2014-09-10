from mpi4py import MPI

comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()
data = (rank+1)**2
data = comm.gather(data, root=0)
print (str(data))
if rank == 0:
	print (str(data))
	for i in range(size):
		assert data[i] == (i+1)**2
else:
	assert data is None
