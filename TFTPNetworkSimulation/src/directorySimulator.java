import java.io.File;


public class directorySimulator {
	private String dir;
	private static File clientFolder;
	private static File serverFolder;
	private static boolean isCDirCreated;
	public directorySimulator ()
	{
	dir=System.getProperty("user.home" ) + "/desktop";
	if(dir.contains("null"))
	{
		System.out.print("desktop dir does not exist. Home dir used instead!");
		dir=System.getProperty("user.home");
	}
	clientFolder= new File(dir, "Client");

	serverFolder= new File(dir, "Server");
	
	clientFolder.mkdir();
	serverFolder.mkdir();
	}
	public static File getClientFolder()
	{
		return clientFolder;
	}
	public static File getServerFolder()
	{
		return serverFolder;
	}
	public static boolean existDir(){
		return isCDirCreated;
	}

}
