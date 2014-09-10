from mpi4py import MPI

class your_class:

	def __init__(self):
		return

	#comm es el objeto communicador que permite envíar mensajes a los demás procesos
	
	def bucket_sort(self,data,comm):
		#data es el subarreglo asignado al proceso
		#comm permite comunicarse con los demas procoesos
		return

	def sample_sort(self,data,n_samples,comm):
		#n_samples es el número de muestras que se tomaran
		#n_samples>comm.Get_size()
		#m lo dejaremos como comm.Get_size()-1
		return

	def sparse_graph_coloring(self,vector,comm):
		#vector es el vector de adyacencia del nodo asociado a comm.Get_rank()
		return

	def shortest_paths(self,vector,comm):
		#vector es el vector de distancia a todos los demas nodos.La distancia al
		#mismo nodo es 0 y a los que no está conectado tambien
		return