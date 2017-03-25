"""
Run this script to convert a text file generated from the provided Logger app into three list of x, y and z readings

author: Delvin Low
"""
import re

# Open Data File and read in a single line # TODO: use glob and a loop to process ALL text files in a path
with open ("Front-0-file.txt", "r") as myfile:
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


# TODO: To pass each of this 3 arrays into Buffering and Feature Extraction. Use the results for SVM