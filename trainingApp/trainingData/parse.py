"""
Run this script to convert a text file generated from the provided Logger app into three list of x, y and z readings

author: Delvin Low
"""
import re

import glob

def write_array_to_file(file, array):
	"""Write the items of an array to the file"""
	array = [str(x) for x in array]
	items = " ".join(array)
	items = items +"\n"
	file.write(items)


text_files = glob.glob("*.txt")
text_files.remove("results.txt")

file_results = open("results.txt", "w")

for text_file in text_files:
	label = text_file.split("-")[0] # Label for action
	# Open Data File and read in a single line # TODO: use glob and a loop to process ALL text files in a path
	with open (text_file, "r") as myfile:
		data = myfile.read()

	# Instantiate regular expression pattern to find float
	accelerometer_regex = re.compile(r'(-)?(\d+(\.\d*)?|\.\d+)([eE](-)?\d+)?')

	# Find all float
	list_of_strings = accelerometer_regex.findall(data)

	# Remove first number which is Fs
	del list_of_strings[0]

	# Go through entire data, put each of the three components readings into a list 
	list_x_readings = []
	list_y_readings = []
	list_z_readings = []
	count = 0
	for sign, reading, redudant_match, redudant_match2, redudant_match3 in list_of_strings:
		value = sign + reading

		count = count % 3

		if count == 0:
			list_x_readings.append(float(value))
		elif count == 1:
			list_y_readings.append(float(value))
		else:
			list_z_readings.append(float(value))

		count += 1

	print "x: ", list_x_readings
	print "y: ", list_y_readings
	print "z: ", list_z_readings


	# Append all label and 3 arrays into text file to pass each of this 3 arrays into Buffering and Feature Extraction on Android
	file_results.write(label +"\n")
	write_array_to_file(file_results, list_x_readings)
	write_array_to_file(file_results, list_y_readings)
	write_array_to_file(file_results, list_z_readings)

file_results.close()