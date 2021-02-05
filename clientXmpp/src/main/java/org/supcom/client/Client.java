



package org.supcom.client;
import org.osgi.framework.*;
import org.supcom.osgidemo.service.definition.Greeter;





public class Client implements BundleActivator, ServiceListener {

    private BundleContext ctx;
    private ServiceReference serviceReference;

    public void start(BundleContext ctx) {
        this.ctx = ctx;
        try {
            ctx.addServiceListener(this, "(objectclass=" + Greeter.class.getName() + ")");
        } catch (InvalidSyntaxException ise) {
            ise.printStackTrace();
        }
    }

    public void stop(BundleContext bundleContext) {
        if (serviceReference != null) {
            ctx.ungetService(serviceReference);
        }
        this.ctx = null;
    }

    public void serviceChanged(ServiceEvent serviceEvent) {
        int type = serviceEvent.getType();
        switch (type) {
            case (ServiceEvent.REGISTERED):
                System.out.println("registration of sevice");
                serviceReference = serviceEvent.getServiceReference();
                Greeter service = (Greeter) (ctx.getService(serviceReference));
                System.out.println(service.sayHiTo("mohaned abid et lamjed gaidi"));
                break;
            case (ServiceEvent.UNREGISTERING):
                System.out.println("unregestration of service");
                ctx.ungetService(serviceEvent.getServiceReference());
                break;
            default:
                break;
        }
    }
}
