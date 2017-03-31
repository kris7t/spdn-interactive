package hu.bme.mit.inf.petridotnet.spdn;

/**
 * Created by kris on 3/29/17.
 */
public class Reward {
    private final String configurationName;
    private final RewardKind kind;

    private Reward(String configurationName, RewardKind kind) {
        this.configurationName = configurationName;
        this.kind = kind;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public RewardKind getKind() {
        return kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reward reward = (Reward) o;

        return (configurationName != null ? configurationName.equals(reward.configurationName) : reward.configurationName == null) && kind == reward.kind;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "configurationName='" + configurationName + '\'' +
                ", kind=" + kind +
                '}';
    }

    @Override
    public int hashCode() {
        int result = configurationName != null ? configurationName.hashCode() : 0;
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        return result;
    }

    public static Reward of(String configurationName, RewardKind kind) {
        return new Reward(configurationName, kind);
    }

    public static Reward instantaneous(String configurationName) {
        return Reward.of(configurationName, RewardKind.INSTANTANEOUS);
    }

    public static Reward accumulated(String configurationName) {
        return Reward.of(configurationName, RewardKind.ACCUMULATED);
    }
}
