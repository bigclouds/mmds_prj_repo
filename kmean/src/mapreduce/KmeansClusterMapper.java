package mapreduce;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import vector.VectorDoubleWritable;
import clusterer.KmeansCluster;
import clusterer.KmeansClusterer;
import config.Constants;
import distanceMeasure.DistanceMeasure;
import distanceMeasure.EuclideanDistance;

public class KmeansClusterMapper extends
		Mapper<LongWritable, Text, Text, KmeansCluster> {
	private VectorDoubleWritable point = null;
	protected KmeansClusterer clusterer = new KmeansClusterer();
	protected Text outkey = new Text();

	public void map(LongWritable key, Text values, Context context)
			throws IOException {
		point = new VectorDoubleWritable(values);

		KmeansCluster cluster = null;
		try {
			cluster = clusterer.findNearestCluster(point);
			// TODO Find a proper way to represent the clustering result.
			System.out.println(((FileSplit) context.getInputSplit()).getPath()
					.getName());
			outkey.set(((FileSplit) context.getInputSplit()).getPath()
					.getName() + "," + key.get());
			context.write(outkey, cluster);
		} catch (IllegalStateException e) {
			System.err.println("Error:\t" + e.getMessage() + " at row(" + key
					+ ")");
		} catch (InterruptedException e) {
			// TODO
			e.printStackTrace();
		}
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		DistanceMeasure dm = null;
		try {
			dm = (DistanceMeasure) Class.forName(
					"distanceMeasure."
							+ conf.get(Constants.DISTANCE_MEASURE,
									"EuclideanDistance")).newInstance();
		} catch (InstantiationException e) {
			dm = new EuclideanDistance();
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			dm = new EuclideanDistance();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			dm = new EuclideanDistance();
			e.printStackTrace();
		}

		this.clusterer = new KmeansClusterer(dm);

		String clusterPath = conf.get(Constants.CLUSTER_PATH);
		if (clusterPath != null && !clusterPath.isEmpty())
			try {
				this.clusterer.loadClusters(clusterPath, conf);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	}
}
