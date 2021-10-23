package net;

/**
 * TODO rm later
 * This is an example showing how to use
 * messages
 */
public class Example {
    public static void run() {
        // Messages can be created in a couple of ways
        // Here is header only message
        var headerOnly = new Message<>(MessageTypes.ERROR, null);

        // Content can be a raw type or a class - the latter is recommended in most cases
        var withContent = new Message<>(MessageTypes.SERVER_HELLO, new ExampleClass(5, "Hello"));

        // Alternatively, instead of creating a class, you can create custom messages through Builder
        // Such messages have content of type GenericMsgContent
        MessageBuilder builder = new MessageBuilder(MessageTypes.LOBBY_DATA);
        builder.addField("someInt", 1);
        builder.addField("someString", "Hello");
        builder.addField("someObject", new ExampleClass(7, "Hello World"));
        var customMessage = builder.get();

        // To hide conversion logic, we can use MessageParser
        // to convert between json string and message object

        // Due to limitations of runtime type deduction in java,
        // we need to provide information about type of content in constructor
        // it is needed only for conversion from json string to object,
        // not the other way round
        // This if the first example
        MessageParser<ExampleClass> msgParser = new MessageParser<>((Class<ExampleClass>) withContent.content.getClass());
        System.out.println("This is header only msg: " + msgParser.toJsonString(headerOnly));
        String string = msgParser.toJsonString(withContent);
        System.out.println("This is msg with content: " + string);
        String genericString = msgParser.toJsonString(customMessage);
        System.out.println("This is msg with generic content: " + genericString);

        // You can also convert to Message Object
        Message<ExampleClass> converted = msgParser.fromJsonString(string);
        System.out.println(converted.content.b);

        // This is another way of providing type information to class, probably prettier
        MessageParser<GenericMsgContent> genericParser = new MessageParser<>(GenericMsgContent.class);
        Message<GenericMsgContent> convertedGeneric = genericParser.fromJsonString(genericString);
        System.out.println(msgParser.jsonElementToObject(convertedGeneric.content.get("someInt"), Integer.class));
        System.out.println(msgParser.jsonElementToObject(convertedGeneric.content.get("someString"), String.class));

        var myConvertedElement = msgParser.jsonElementToObject(convertedGeneric.content.get("someObject"), ExampleClass.class);
        System.out.println(myConvertedElement.b);

        // While surrounding parsing with try catch isn't currently enforced,
        // it is recommended, as issues might appear if types don't match
        try {
            var myIncorrectElement = msgParser.jsonElementToObject(convertedGeneric.content.get("someInt"), ExampleClass.class);
        } catch (Exception ex) {
            System.out.println("This exception was expected: " + ex.getMessage());
        }
    }

    public static class ExampleClass {
        private final int a;
        private final String b;
        private final Integer[] c = {1,2,3,4};

        public ExampleClass(int a, String b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public Integer[] getC() {
            return c;
        }
    }
}
