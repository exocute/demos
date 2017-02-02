# VideoCompressor
-------------------------------------------------------------------------------
## Requirements
ExoNode should be working at localHost</br>

## How to Use?
run videoCompressor {inputFilePath} {resultFilePath}</br>

## How does it works?
videoCompressor:</br>
1 - uses HumbleVideo to decode all frames of the input file and FFMPEG to decode the audio from the file</br>
2 - sends all pairs of consecutive frames to exocute (frame1,frame2)</br>
3 - uses exocute to divide the image in the four quadrants, analyse the differences of each quadrant, calculate the average of every quadrant and then decide if thepair has a keyframe on frame2</br>
4 - using JCodec, encodes all frames back together with the information if every frame should be a keyFrame or not, FFMPEG encodes the audio</br>
5 - results in a compressed video file</br>

Check VideoCompression.png, compress.grp for## a better understanding </br>
