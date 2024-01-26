package pipeline.common.constants

class AppMode {
    public static final String APP_MODE_NONE = 'None'
    public static final String APP_MODE_RESOURCE_APP = 'ResourceApp'
    public static final String APP_MODE_APP = 'App'
    public static final String APP_MODE_BUNDLE = 'Bundle&Upload'
    public static final String APP_MODE_APP_AND_BUNDLE = 'App&Bundle&Upload'
    public static final ArrayList<String> APP_MODE_LIST = [
            APP_MODE_NONE,
            APP_MODE_RESOURCE_APP,
            APP_MODE_APP,
            APP_MODE_BUNDLE,
            APP_MODE_APP_AND_BUNDLE
    ]
}
