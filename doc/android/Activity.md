# Activity

 Activity class takes care of creating a window for you in which you can place your UI with {@link #setContentView}

 每一个 Activity 持有一个 PhoneWindow 的对象，
 而一个 PhoneWindow 对象持有一个 DecorView 的实例，
 所以 Activity 中 View 相关的操作其实大都是通过 DecorView 来完成。