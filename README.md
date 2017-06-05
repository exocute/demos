# VideoCompressor

## Requirements
ExoNode should be working at localhost</br>

## How to Use?
run CompressDemoParallel [fps]</br>
run CompressDemoUsingSwave [fps]</br>
default fps = 25</br>

## How does it works?
CompressDemoParallel:</br>
1 - uses HumbleVideo to decode all frames of the input file and FFMPEG to decode the audio from the file</br>
2 - sends all pairs of consecutive frames to exocute (frame 1,frame 2),(frame 2,frame 3), ..., (frame n-1, frame n)</br>
3 - uses exocute to analyse the differences of each pair of frames, and then decide if the pair has a keyframe on frame2</br>
4 - using JCodec, encodes all frames back together with the information if every frame should be a keyFrame or not, FFMPEG encodes the audio</br>
5 - results in a compressed video file</br>

CompressDemoUsingSwave:</br>
  all the same tasks are done using swave transformations.
 
Check VideoCompression.png, compress.grp for a better understanding </br>
Check VideoCompressionParallel.png(available soon) and compressParallel for a better understanding </br>

### Swave conversion examples
In the swaveToExocuteDemo folder we have several examples 
of using the toolkit conversions from Swave to ExoGraph.<br>

This examples show that the ExoGraph resulted from the conversions produce
the same result has the original Swave running locally.

