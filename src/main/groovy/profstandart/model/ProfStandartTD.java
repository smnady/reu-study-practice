package profstandart.model;


public record ProfStandartTD(
        String code,
        String name
) {
    @Override
    public String toString() {
        return (code != null && !code.isEmpty() ? code + ": " : "") + name;
    }
}
