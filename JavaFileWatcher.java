import java.io.IOException;
import java.nio.file.*;

// Required imports for IO Exception and file handling

public class JavaFileWatcher {

    public static final String directoryPath = "C:\\Users\\olive\\OneDrive\\Desktop\\FileTester";

    // Directory path is created for files in the 'FileTester' folder on my Desktop

    public static void main(String[] args) {
        System.out.println("Watch Service started");

    // Main method and print statement to indicate that the Watch Service has started

        try{
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Creating the watchService, which will monitor the directory from the directoryPath, if any changes occur (i.e. File creation, modification, or deletion)

        Path path = Paths.get(directoryPath);
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        // Registering the path to the watchService and specifying the events to watch for
        while(true) {

            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                Path file = path.resolve((Path)event.context());
                System.out.println("Event : " + event.kind());
                System.out.println("Location = " + file.toFile().getAbsolutePath());
                System.out.println("Name = " + file.toFile().getName());
                System.out.println("Timestamp = " + file.toFile().lastModified());

            // Seperated output statements for each event for formatting purposes
            }
            key.reset();

            // Resetting the key, so future events can be watched for
        }

    } catch (IOException e) {
        e.printStackTrace();

    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Catching of Exceptions for Error Handling


    }
}