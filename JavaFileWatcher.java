import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Required imports for IO Exception and file handling

public class JavaFileWatcher {

    public static int ID = 1;

    private static final Map<String, PendingModify> pendingMods = new HashMap<>();
    private static final long DEBOUNCE_MS = 800;
    private static final Map<WatchKey, Path> watchKeys = new HashMap<>();

    public static final String directoryPath = "C:\\Users\\olive\\OneDrive\\Desktop\\FileTester";

    // Directory path is created for files in the 'FileTester' folder on my Desktop

    private static ArrayList<EventFormatter> eventHistory = new ArrayList<>();
    public static void main(String[] args) {

        FileWatcherGUI gui = new FileWatcherGUI();
        System.out.println("Monitoring for file activity in" + directoryPath + "...");

    // Main method and print statement to indicate that the Watch Service has started

        try{
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Creating the watchService, which will monitor the directory from the directoryPath, if any changes occur (i.e. File creation, modification, or deletion)

        Path root = Paths.get(directoryPath);

        Files.walk(root).filter(Files::isDirectory).forEach(dir -> {
            try{
                WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                watchKeys.put(key, dir);
                System.out.println("Watching: " + dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nClosing file watcher...");
            printSummaryStats();
            exportToCSV();
        }));

        // Registering the path to the watchService and specifying the events to watch for
        while(true) {

            WatchKey key = watchService.take();

            Path currentDir = watchKeys.get(key);

            for (WatchEvent<?> event : key.pollEvents()) {

                if(event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                
                Path file = currentDir.resolve((Path) event.context());
                
                String relativePath = root.relativize(file).toString();

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(file)) {

                    Files.walk(file).filter(Files::isDirectory).forEach(dir -> {
                        try {
                            WatchKey newkey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                            watchKeys.put(newkey, dir);
                            System.out.println("Now watching: " + dir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                    });
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) 
                {

                    String fileKey = file.toAbsolutePath().toString();

                    PendingModify existing = pendingMods.get(fileKey);

                    if (existing == null) {
                        pendingMods.put(fileKey, new PendingModify(fileKey));
                    } else {
                        existing.update();
                    }

                    continue;
                }

                long fileSize = 0;

                if (Files.exists(file))
                {
                    fileSize = Files.size(file);
                }

                EventFormatter eventFormatter = new EventFormatter(
                    event.kind().name(),
                    file.getFileName().toString(),
                    LocalDateTime.now(),
                    file.toAbsolutePath().toString(),
                    fileSize,
                    ID
                );

                eventHistory.add(eventFormatter);
                ID++;

                System.out.println("_______________________________");
                System.out.println("");
                System.out.println(eventFormatter);
                gui.addEvent(eventFormatter);

            // Seperated output statements for each event for formatting purposes
            }

            long now = System.currentTimeMillis();

            Iterator<Map.Entry<String, PendingModify>> it = pendingMods.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, PendingModify> entry = it.next();
                PendingModify mod = entry.getValue();

                if (now - mod.lastEventTime >= DEBOUNCE_MS) {

                    Path file = Paths.get(mod.filePath);
                    long size = Files.exists(file) ? Files.size(file) : 0;

                    EventFormatter event = new EventFormatter("ENTRY_MODIFY (x" + mod.count.get() + ")", file.getFileName().toString(), LocalDateTime.now(), file.toAbsolutePath().toString(), size, ID++);

                    eventHistory.add(event);

                    System.out.println("_______________________________");
                    System.out.println(event);

                    gui.addEvent(event);

                    it.remove();
                }
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

    public static void printSummaryStats() {

        Map<String, Integer> fileActivity = new HashMap<>();

        int created = 0;
        int modified = 0;
        int deleted = 0;

        for (EventFormatter event : eventHistory)
        {

            String fileName = event.getFileName();

            fileActivity.put(fileName, fileActivity.getOrDefault(fileName, 0) +1 );

            String type = event.getEventType();

            if (type.equals("ENTRY_CREATE")) 
                {
                created++;
            }
            else if (type.startsWith("ENTRY_MODIFY"))
            {
                modified++;
            }
            else if (type.equals("ENTRY_DELETE"))
            {
                deleted++;
            }
        }

        String mostActiveFile = "None";
        int highestCount = 0;

        for (Map.Entry<String, Integer> entry : fileActivity.entrySet())
        {

            if (entry.getValue() > highestCount)
            {
                highestCount = entry.getValue();
                mostActiveFile = entry.getKey();
            }
        }
        
        System.out.println("\n=================================");
        System.out.println("        EVENT SUMMARY");
        System.out.println("=================================");
        System.out.println("Files Created : " + created);
        System.out.println("Files Modified: " + modified);
        System.out.println("Files Deleted : " + deleted);
        System.out.println("Total Events  : " + eventHistory.size());
        System.out.println("File with most Activity : " + mostActiveFile + " (" + highestCount + " events)");
        System.out.println("=================================");
    }

    public static void exportToCSV() {

    Path output = Paths.get("EventHistory.csv");
    System.out.println("CSV saved to: " + output.toAbsolutePath());

    try (BufferedWriter writer = Files.newBufferedWriter(output)) {

        writer.write("ID,Event Type,File Name,Timestamp,File Size,Path");
        writer.newLine();

        for (EventFormatter event : eventHistory) {

            writer.write(
                event.getID() + "," +
                event.getEventType() + "," +
                event.getFileName() + "," +
                event.getEventTime() + "," +
                event.getFileSize() + "," +
                "\"" + event.getFilePath() + "\""
            );

            writer.newLine();
        }

        System.out.println("Event history exported to " + output.toAbsolutePath());

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
}