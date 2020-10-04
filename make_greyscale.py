import os, sys
from PIL import Image

BASE_DIR = os.path.join(os.getcwd(),"desktop/assets/sprites/pokemon/icon")

def make_greyscale(fpath:str):
	output_name = os.path.join(BASE_DIR,get_grey_scale_name(fpath))
	img = Image.open(fpath).convert('LA')
	img.save(output_name)
	

def get_grey_scale_name(fpath:str):
	file_name = fpath.split('/')[-1]
	file_name = file_name.split('.')
	file_ext = file_name[1]
	file_name = file_name[0]

	if file_name[-2:] == '_g':
		return ".".join([file_name,file_ext])
	else:
		return ".".join([file_name+"_g",file_ext])

def main():
	for file in os.listdir(BASE_DIR):
		make_greyscale(os.path.join(BASE_DIR,file))

main()
