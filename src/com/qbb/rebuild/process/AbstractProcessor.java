package com.qbb.rebuild.process;

import com.intellij.notification.*;
import com.intellij.psi.PsiMethod;
import com.qbb.rebuild.ApiBuildContext;

/**
 * @Author: chong.zhang
 * @Date: 2021-04-10 16:36:18
 */

public abstract class AbstractProcessor<T> {

    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("处理节点发生异常", NotificationDisplayType.BALLOON, true);
    }

    /**
     * 下一个处理节点
     */
    private AbstractProcessor nextProcesser = null;

    /**
     * 是否在本节点成功处理
     */
    private boolean status = false;

    public final void process(T t, ApiBuildContext context, PsiMethod psiMethod){
        try{
            this.realProcess(t, context, psiMethod);
        }catch (Exception e){
            String message = context.getSelectedText() + "方法处理异常：" + e.getMessage();
            Notification error = notificationGroup.createNotification(message, NotificationType.ERROR);
            Notifications.Bus.notify(error);
        }
        if (!status && nextProcesser != null) {
            nextProcesser.process(t, context, psiMethod);
        }
    }

    protected void done(){
        this.status = true;
    }

    public void setNextProcesser(AbstractProcessor nextProcesser) {
        this.nextProcesser = nextProcesser;
    }

    /**
     * 节点做的处理
     */
    abstract void realProcess(T t, ApiBuildContext context, PsiMethod psiMethod) throws Exception;
}
