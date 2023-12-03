import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;

public class WebcamResolutionExample {

    public static void main(String[] args) {
        Webcam webcam = Webcam.getDefault();

        if (webcam != null) {
            System.out.println("Default Webcam: " + webcam.getName());

            Dimension[] sizes = webcam.getViewSizes();
            if (sizes.length > 0) {
                System.out.println("Supported Resolutions:");
                for (Dimension size : sizes) {
                    System.out.println(size.width + "x" + size.height);
                }

                // Set the webcam to the highest supported resolution
                webcam.setViewSize(sizes[sizes.length - 1]);

                System.out.println("Selected Resolution: " + webcam.getViewSize());
            } else {
                System.out.println("No supported resolutions found");
            }
        } else {
            System.out.println("No webcam detected");
        }
    }
}
