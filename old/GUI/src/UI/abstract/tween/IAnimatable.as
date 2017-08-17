package UI.abstract.tween
{
    /** 
	 * IAnimatable接口定义了这样的一些对象：基于一个时间范围实现动画过程。
	 * 任何实现了这个接口的类的实例，都可以被添加到TimeLine。
	 * 当一个对象不再需要运动的时候，你应当将它从TimeLine删除。
	 * 要实现这一点，你可以手动执行这个方法：TimeLine.remove(object) 来删除它， 
	 * 或者让这个对象派发一个事件来请求TimeLine删除自己，事件类型是TimeLineEvent.REMOVE_FROM_TIMELINE
     */
    public interface IAnimatable 
    {
        /** 两帧间隔 **/
        function advanceTime(time:Number):void;
    }
}