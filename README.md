# DecodeAndroidGesture

This tool decode Android's pattern lock screen with gesture.key file in Android device.
It just decode 3x3 dimensions schema.

# Usage

You can copy Android's file at /data/system/gesture.key in your computer.
And run jar file : 
  java -jar DecodeAndroidGesture.jar gesture.key

After a few instant, the tool return digits sequence.
Each digits represent a ball in your Android's pattern lock screen.

Android's pattern lock screen can be represented by this schema : 

  -------------
  | 0 | 1 | 2 |
  -------------
  | 3 | 4 | 5 |
  -------------
  | 6 | 7 | 8 |
  -------------
  
In this schema, each digits represent a ball.

