package hu.bme.mit.inf.petridotnet.spdn;

/**
 * Created by kris on 3/29/17.
 */
public enum RewardKind {
    INSTANTANEOUS("inst"),
    ACCUMULATED("acc");

    private final String shortName;

    RewardKind(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
