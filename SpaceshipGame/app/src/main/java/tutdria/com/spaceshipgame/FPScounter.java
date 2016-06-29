package tutdria.com.spaceshipgame;

final public class FPScounter {
        private static int startTime;  
        private static int endTime;  
        private static int frameTimes = 0;  
        private static short frames = 0;
        private static int retFrames = 0;

        public final static void StartCounter() {
            startTime = (int) System.currentTimeMillis();  
        }  

        public final static int StopAndPost() {
            endTime = (int) System.currentTimeMillis();
            frameTimes = frameTimes + endTime - startTime;
            ++frames;
            if(frameTimes >= 1000) {
                retFrames = frames;
                frames = 0;
                frameTimes = 0;  
            }
            return retFrames;
        }
    }