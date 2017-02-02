## VideoCompression
A simple video compressor using exocute
-------------------------------------------------------------------------------
## Requirements
ExoNode should be working at localHost
videoCompressor

## How to Use?
run videoCompressor {inputFilePath} {resultFilePath}
f
## How does it works?
videoCompressoffefer:
1 - uses HumbleVideo to decode all frames of the input file and FFMPEG to decode the audio from the file
2 - sends all pairs of consecutive frames to exocute (frame1,frame2)
3 - uses exocute to divide the image in the four quadrants, analyse the differences of each quadrant, calculate the average of every quadrant and then decide if thepair has a keyframe on frame2
4 - using JCodec, encodes all frames back together with the information if every frame should be a keyFrame or not, FFMPEG encodes the audio
5 - results in a compressed video file

CHECK VideoCompression.png, compress.grp for## a better understanding 
