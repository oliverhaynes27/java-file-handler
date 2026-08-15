import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventFormatter
{

    private final String eventType;
    private final String fileName;
    private final LocalDateTime eventTime;
    private final String filePath;
    private final long fileSize;
    private final String relativePath;
    private final String fileExtension;
    private final int ID;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public EventFormatter(String eventType, String fileName, LocalDateTime eventTime, String filePath, long fileSize, String relativePath, String fileExtension, int ID)
    {
        this.eventType = eventType;
        this.fileName = fileName;
        this.eventTime = eventTime;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.relativePath = relativePath;
        this.fileExtension = fileExtension;
        this.ID = ID;
    }

    public String getEventType() 
    {
        return eventType;
    }

    public String getFileName()
    {
        return fileName;
    }

    public LocalDateTime getEventTime()
    {
        return eventTime;
    }
    
    public String getFilePath()
    {
        return filePath;
    }

    public long getFileSize()
    {
        return fileSize;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public String getFileExtension()
    {
        return fileExtension;
    }

    public int getID()
    {
        return ID;
    }

    @Override
    public String toString()
    {
        return "Event: " + eventType +
               "\nFile: " + fileName +
               "\nExtension: " + fileExtension +
               "\nTime: " + eventTime.format(FORMAT) +
               "\nPath: " + filePath +
               "\nRelative Path: " + relativePath +
               "\nSize: " + fileSize + " bytes" +
               "\nEvent ID: " + ID;
    }

}