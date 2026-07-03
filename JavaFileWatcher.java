import java.io.IOException;
import java.nio.file.*;

public class JavaFileWatcher {

    public static final String directoryPath = "C:\\Users\\olive\\OneDrive\\Desktop\\incoming";


    public static void main(String[] args) {
        System.out.println("Watch Service started");

        try{
        WatchService watchService = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(directoryPath);
        WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        while(true) {
            for(WatchEvent<?> event : watchKey.pollEvents() ) {
                System.out.println("Event type/kind : " + event.kind() + "file affected : " + event.context());
            
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    }
}