/**
 * 
 */
package zzz;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import org.apache.cassandra.thrift.Cassandra;
//import org.apache.cassandra.thrift.Clock;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * run cassandra with -f to redirect log to stdout
 * bin/cassandra -f
 * 
 * create keyspace and column family with cassandra-cli (>0.7)
 * 
 * bin/cassandra-cli
 * Welcome to cassandra CLI.
 * 
 * Type 'help;' or '?' for help. Type 'quit;' or 'exit;' to quit.
 * 
 * [default@unknown] connect localhost/9160;
 * Connected to: "ZenCluster" on localhost/9160
 * 
 * [default@unknown] create keyspace Keyspace1;
 * 679d7c8e-273c-11e0-99e9-e700f669bcfc
 *
 * [default@unknown] use Keyspace1;
 * Authenticated to keyspace: Keyspace1
 *
 * [default@Keyspace1] create column family Standard1;
 * 9be2ae7f-273c-11e0-99e9-e700f669bcfc
 *
 *
 * 
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class RwTest
{
	private static final String UTF8 = "UTF8";
	private static final String HOST = "localhost";
	private static final int PORT = 9160;
	private static final ConsistencyLevel CL = ConsistencyLevel.ONE;
	
   public static Charset charset = Charset.forName("UTF-8");
   public static CharsetEncoder encoder = charset.newEncoder();
   public static CharsetDecoder decoder = charset.newDecoder();

	//not paying attention to exceptions here
	
	public static void main(String[] args) throws UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException, NotFoundException, CharacterCodingException {

	TTransport tr = new TSocket(HOST, PORT);  //new default in 0.7 is framed transport  
	TFramedTransport tf = new TFramedTransport(tr);  
	TProtocol proto = new TBinaryProtocol(tf);  
	Cassandra.Client client = new Cassandra.Client(proto);  tf.open();  
	client.set_keyspace("Keyspace1");
	String cfName = "Standard1";  
	byte[] userIDKey = "1".getBytes(); //this is a row key

	//create a representation of the Name column  
	ColumnPath colPathName = new ColumnPath(cfName); 
	colPathName.setColumn("name".getBytes(UTF8));  
	ColumnParent cp = new ColumnParent(cfName);

	//insert the name column  
	System.out.println("Inserting row for key " + new String(userIDKey)); 
	client.insert(ByteBuffer.wrap(userIDKey), cp,  new Column(ByteBuffer.wrap("name".getBytes(UTF8)),  ByteBuffer.wrap("George Clinton".getBytes()), getNow()), CL);

	//insert the Age column
	client.insert(ByteBuffer.wrap(userIDKey), cp,  new Column(ByteBuffer.wrap("age".getBytes(UTF8)),  ByteBuffer.wrap("69".getBytes()), getNow()), CL);
	
	// read just the Name column  
	System.out.println("Reading Name Column:");  
	Column col = client.get(ByteBuffer.wrap(userIDKey), colPathName, CL).getColumn();
	System.out.println("Column name: " + decoder.decode(col.name));  
	System.out.println("Column value: " + decoder.decode(col.value));  
	System.out.println("Column timestamp: " + col.timestamp);

	//create a slice predicate representing the columns to read  
	//start and finish are the range of columns--here, all  
	SlicePredicate predicate = new SlicePredicate();  
	SliceRange sliceRange = new SliceRange(); sliceRange.setStart(new byte[0]);  
	sliceRange.setFinish(new byte[0]);  
	predicate.setSlice_range(sliceRange);

	System.out.println("Complete Row:");  // read all columns in the row  
	ColumnParent parent = new ColumnParent(cfName);  
	List<ColumnOrSuperColumn> results = client.get_slice(ByteBuffer.wrap(userIDKey), parent, predicate, CL);  

	//loop over columns, outputting values  
	for (ColumnOrSuperColumn result : results) {     
	  Column column = result.column;      
	  //System.out.println(column.name + " : " + column.value);  
	  System.out.println(decoder.decode(column.name) + " : " + decoder.decode(column.value));  
	
	}
	tf.close();  

	System.out.println("All done."); }

	/**
	 * @return
	 */
	private static long getNow()
	{
		return System.currentTimeMillis();
	}
}
