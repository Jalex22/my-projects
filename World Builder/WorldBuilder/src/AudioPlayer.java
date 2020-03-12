
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer 
{
	
	//loops to play sound continuously
	public static synchronized void playLoopSound(AudioClip sfx)
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				try
				{
					AudioInputStream stream = sfx.getAudioStream();
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.loop(Clip.LOOP_CONTINUOUSLY);
					clip.start();
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}; thread.start();
	}
	
	//plays sound once
	public static synchronized void playSound(AudioClip sfx)
	{
	
		Thread thread = new Thread()
		{
			public void run()
			{
				try
				{
					AudioInputStream stream = sfx.getAudioStream();
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}; thread.start();
	}
}
