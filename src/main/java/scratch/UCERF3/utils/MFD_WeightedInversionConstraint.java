package scratch.UCERF3.utils;
import org.dom4j.Element;
import java.io.IOException;
import org.opensha.commons.data.function.EvenlyDiscretizedFunc;
import org.opensha.commons.geo.Region;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import com.google.common.base.Preconditions;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * This class extends MFD_InversionConstraint (having an MFD and a Region) with an EvenlyDiscretized function 
 * containing weights.
 * 
 * @author chrisbc
 *
 */
@JsonAdapter(MFD_WeightedInversionConstraint.Adapter.class)
public class MFD_WeightedInversionConstraint extends MFD_InversionConstraint {
	
	public static final String XML_METADATA_NAME = "MFD_WeightedInversionConstraint";
	
	EvenlyDiscretizedFunc weights;
	
	public MFD_WeightedInversionConstraint(IncrementalMagFreqDist mfd, Region region, EvenlyDiscretizedFunc weights) {
		super(mfd, region);
		this.weights=weights;
		validateDiscretization();
	}
	
	private void validateDiscretization() {
		Preconditions.checkState(mfd.getMinX() == weights.getMinX(), "minX of mfd and weight objects must be equal", mfd.getMinX(), weights.getMinX());
		Preconditions.checkState(mfd.getMaxX() == weights.getMaxX(), "maxX of mfd and weight objects must be equal", mfd.getMaxX(), weights.getMaxX());
		Preconditions.checkState(mfd.size() == weights.size(), "size of mfd and weight objects must be equal", mfd.size(), weights.size());
	}
	
	public void setWeights(EvenlyDiscretizedFunc weights) {
		this.weights=weights;
		validateDiscretization();
	}
	
	
	public EvenlyDiscretizedFunc getWeights() {
		return weights;
	}
	
	@Deprecated
	@Override
	public Element toXMLMetadata(Element root) {
		throw new UnsupportedOperationException("No more XML, sorry");
	}
	
	public static class Adapter extends TypeAdapter<MFD_WeightedInversionConstraint> {

		private TypeAdapter<IncrementalMagFreqDist> mfdAdapter = new IncrementalMagFreqDist.Adapter();
		private TypeAdapter<Region> regionAdapter = new Region.Adapter();
		private TypeAdapter<EvenlyDiscretizedFunc> weightsAdapter = new EvenlyDiscretizedFunc.Adapter();

		@Override
		public void write(JsonWriter out, MFD_WeightedInversionConstraint value) throws IOException {
			out.beginObject();
			
			out.name("mfd");
			mfdAdapter.write(out, value.mfd);

			out.name("weights");
			weightsAdapter.write(out, value.weights);
	
			out.name("region");
			if (value.region == null) {
				out.nullValue();
			} else {
				// this will convert a GriddedRegion to a plain region, if needed
				Region region = new Region(value.region);
				regionAdapter.write(out, region);
			}
			
			out.endObject();
		}

		@Override
		public MFD_WeightedInversionConstraint read(JsonReader in) throws IOException {
			in.beginObject();
			
			IncrementalMagFreqDist mfd = null;
			Region region = null;
			EvenlyDiscretizedFunc weights = null;
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "mfd":
					mfd = mfdAdapter.read(in);
					break;
				case "region":
					region = regionAdapter.read(in);
					break;
				case "weights":
					weights = weightsAdapter.read(in);
					break;
				default:
					in.skipValue();
					break;
				}
			}
			Preconditions.checkNotNull(mfd, "MFD not specified");
			Preconditions.checkNotNull(weights, "Weights not specified");
			
			in.endObject();
			return new MFD_WeightedInversionConstraint(mfd, region, weights);
		}
		
	}

}
