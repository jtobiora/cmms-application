package ng.upperlink.nibss.cmms.util.emandate.soap.interfaces;

/**
 * Created by Nduka on 25/10/2018.
 * Support object template mocking
 */
public interface IObjectMocker<T> {

    T buildMock(Object... args);

    default void validate(int argCount, Object... args){
//        if(args.length < argCount) //throw new BAPException(ExceptionKeyConstant.INVALID_OBJECT_MOCKER_ARGUMENT_COUNT, ImmutableMap.of("expected_args",argCount));
    }

    default Object fake(){
        Double x = Math.random() * 50;
        return null;
    }

}