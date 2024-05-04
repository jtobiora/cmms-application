package ng.upperlink.nibss.cmms.util.emandate;

import lombok.Data;

import java.util.Optional;
@Data
public class OptionalB {
    private  String answer1 = null;
    private  String answer2 = null;

    public static void main(String[] args) {

        Optional<String> gender = Optional.of("MALE");
        OptionalB optionalB = new OptionalB();

        System.out.println("Non-Empty Optional:" + gender);
        System.out.println("Non-Empty Optional: Gender value : " + gender.get());
        System.out.println("Empty Optional: " + Optional.empty());

        System.out.println("ofNullable on Non-Empty Optional: " + Optional.ofNullable(optionalB.getAnswer1()).orElse(null));
        System.out.println("ofNullable on Empty Optional: " + Optional.ofNullable(optionalB.getAnswer2()));

        // java.lang.NullPointerException
//        System.out.println("ofNullable on Non-Empty Optional: " + Optional.of(answer2));

    }

}