package hu.bme.mit.inf.petridotnet.spdn;

/**
 * Created by kris on 3/29/17.
 */
public class Parameter {
    private final String name;

    private Parameter(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        return name != null ? name.equals(parameter.name) : parameter.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                '}';
    }

    public static Parameter ofName(String name) {
        return new Parameter(name);
    }
}
