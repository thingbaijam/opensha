package scratch.UCERF3.utils;

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
	}
	
	public void setWeights(EvenlyDiscretizedFunc weights) {
		this.weights=weights;
	}
	
	
	public EvenlyDiscretizedFunc getWeights() {
		return weights;
	}
	
	
//	public static MFD_WeightedInversionConstraint fromXMLMetadata(Element constrEl) {
//		Element regionEl = constrEl.element(Region.XML_METADATA_NAME);
//		Region region = regionEl == null ? null : Region.fromXMLMetadata(regionEl);
//		
//		Element mfdEl = constrEl.element(IncrementalMagFreqDist.XML_METADATA_NAME);
//		EvenlyDiscretizedFunc func = (EvenlyDiscretizedFunc) EvenlyDiscretizedFunc.fromXMLMetadata(mfdEl);
//		IncrementalMagFreqDist mfd = new IncrementalMagFreqDist(func.getMinX(), func.size(), func.getDelta());
//		for (int i=0; i<func.size(); i++) {
//			mfd.set(i, func.getY(i));
//		}
//		
//		return new MFD_WeightedInversionConstraint(mfd, region);
//	}
	
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
