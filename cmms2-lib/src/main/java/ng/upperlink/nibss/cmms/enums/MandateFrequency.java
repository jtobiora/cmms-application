package ng.upperlink.nibss.cmms.enums;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.mandates.MandateFrequencyResponse;

import java.util.ArrayList;
import java.util.List;

public enum MandateFrequency {
    VARIABLE(0, "Variable"),
	WEEKLY(1, "Weekly"),
	//EVERY_TWO_WEEKLY(2, "Every 2 weeks"),
	MONTHLY(4, "Monthly"),
	//EVERY_TWO_MONTHS(8, "Every 2 months"),
   // EVERY_THREE_MONTHS(12, "Every 3 months"),
    EVERY_FOUR_MONTHS(16, "Every 4 months"),
    //EVERY_FIVE_MONTHS(20, "Every 5 months"),
    EVERY_SIX_MONTHS(24, "Every 6 months"),
   // EVERY_SEVEN_MONTHS(28, "Every 7 months"),
  //  EVERY_EIGHT_MONTHS(32, "Every 8 months"),
  //  EVERY_NINE_MONTHS(36, "Every 9 months"),
  //  EVERY_TEN_MONTHS(40, "Every 10 months"),
  //  EVERY_ELEVEN_MONTHS(44, "Every 11 months"),
    EVERY_TWELVE_MONTHS(48, "Every year");

    int id;
    String description;

    MandateFrequency(int id,String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription(){
        return description;
    }

    public static List<MandateFrequencyResponse> getMandateFrequencies() {
        List<MandateFrequencyResponse> responseList = new ArrayList<>();
        for(MandateFrequency mFreq : MandateFrequency.values()) {
           MandateFrequencyResponse response = new MandateFrequencyResponse();
           response.setDescription(mFreq.getDescription());
           response.setId(mFreq.getId());

           responseList.add(response);
        }
        return responseList;
    }

    public static MandateFrequency findById(int  id) {
        for (MandateFrequency mf : MandateFrequency.values()) {
            if (mf.getId() == id)return mf;
        }
        return null;
    }


}
