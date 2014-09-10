from mpi4py import MPI

class your_class:

	def __init__(self):
		return

	def your_method1(self,data,comm):
		print("This is your method, rank:"+str(comm.Get_rank()))