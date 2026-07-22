import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventFormatter
{

    private String eventType;
    private String fileName;
    private LocalDateTime eventTime;
    private String filePath;
    private long fileSize;
    private String relativePath;
    private String FileExtension;
    private int ID;

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

    public int getID()
    {
        return ID;
    }

    @Override
    public String toString()
    {
        return "Event: " + eventType +
               "\nFile: " + fileName +
               "\nTime: " + eventTime.format(FORMAT) +
               "\nPath: " + filePath +
               "\nSize: " + fileSize + " bytes" +
               "\nEvent ID: " + ID;
    }

}