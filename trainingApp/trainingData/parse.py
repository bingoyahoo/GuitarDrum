"""
Run this Python script to convert all text files generated from the provided Logger app into three list of x, y and z readings and append them into a file called results.txt

author: Delvin Low
"""
import re
import glob
import matplotlib.pyplot as plt
import os

TO_PLOT = True

def write_array_to_file(file, array):
	"""Write the items of an array to the file"""
	array = [str(x) for x in array]
	items = " ".join(array)
	items = items +"\n"
	file.write(items)

# Use glob and a loop to process ALL text files in a path
dir_path = os.path.dirname(os.path.realpath(__file__))
text_files = glob.glob(dir_path + "/*/*.txt")
# print text_files
try:
	text_files.remove("results.txt") # Don't process existing results file
except ValueError:
	pass

file_results = open("results.txt", "w")
for text_file in text_files:
	head, tail = os.path.split(text_file)
	label0 = tail.split("-")[0] # Label for action
	label = label0[: -1] # remove last number
	print label

	# label = label[: -1]
	# Open Data File and read in a single line 
	with open (text_file, "r") as myfile:
		data = myfile.read()

	# Instantiate regular expression pattern to find float
	accelerometer_regex = re.compile(r'(-)?(\d+(\.\d*)?|\.\d+)([eE](-)?\d+)?')

	# Find all float
	list_of_strings = accelerometer_regex.findall(data)

	# Remove first number which is Fs
	fs = list_of_strings[0]
	del list_of_strings[0]
	sampling_freq = fs[1]

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

	# print "x: ", list_x_readings
	# print "y: ", list_y_readings
	# print "z: ", list_z_readings
	if TO_PLOT:
		plt.clf()
		plt.title(label)
		plt.plot(list_x_readings, "r")
		plt.plot(list_y_readings, "g")
		plt.plot(list_z_readings, "b")
		plt.ylabel('Accelerometer values')
		# plt.show()
		plt.savefig(label0 + ".png")


	# Append all label and 3 arrays into text file to pass each of this 3 arrays into Buffering and Feature Extraction on Android
	file_results.write(sampling_freq +"\n")
	file_results.write(label +"\n")
	write_array_to_file(file_results, list_x_readings)
	write_array_to_file(file_results, list_y_readings)
	write_array_to_file(file_results, list_z_readings)

file_results.close()