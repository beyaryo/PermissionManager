# PermissionManager
Library for easy permission managing
## Download 
In your project-level `build.gradle`:
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```
Add dependecies to your module-level `build.gradle`:
```
dependencies {
   implementation 'com.github.beyaryo:PermissionManager:1.0.2'
}
```
## Usage
First you need to implement PermissionListener to your Activity or fragment

```java
public class MyActivity extends AppCompatActivity implements PermissionListener {
    
    @Override
    public void onPermissionGranted(String[] permissions, String tag) {
        // Do something when permission granted
    }

    @Override
    public void onPermissionDenied(String[] permissions, String tag) {
        // Do something when permission denied
    }

    @Override
    public void onPermissionDisabled(String[] permissions, String tag) {
        // Do something when permission disabled
    }
}
```
It will require you to add 3 functions. Those function will be called after user do some action when permission is requested.

**IMPORTANT!!** if you use this library in a `Fragment` make sure to initialize `PermissionManager` class like this
```java
PermissionManager manager = new PermissionManager(your_fragment, your_listener);
``` 

You can check single permission :
```java
PermissionManager manager = new PermissionManager(your_activity, your_listener);

String singlePermission = Manifest.permission.CAMERA;
manager.check(singlePermission, "TAG");
```
Or for multiple permission you can do :
```java
PermissionManager manager = new PermissionManager(your_activity, your_listener);

String[] multiplePermission = {
          Manifest.permission.READ_SMS,
          Manifest.permission.READ_CONTACTS,
          Manifest.permission.WRITE_EXTERNAL_STORAGE};
manager.check(multiplePermission, "TAG");
```
Parameter **TAG** is just for flag to distinguish a process with other process. Or you can just add a simple String like **""**.

**IMPORTANT!!** `check()` method will request the permissions for you, but the result will be returned on `onRequestPermissionsResult()`. So you need to do this on your activity :
```java
PermissionManager manager = new PermissionManager(your_activity, your_listener);
...
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    // REQUIRED
    manager.result(requestCode, permissions, grantResults);
}
```
After that you can do whatever you want on previous 3 method implemented from `PermissionListener` :
```java
@Override
public void onPermissionGranted(String[] permissions, String tag) {
    // Do something when permission granted
}

@Override
public void onPermissionDenied(String[] permissions, String tag) {
    // Do something when permission denied
}

@Override
public void onPermissionDisabled(String[] permissions, String tag) {
    // Do something when permission disabled
}
```
* **onPermissionGranted** : will be called upon some permissions are granted
* **onPermissionDenied** : will be called upon some permissions are denied
* **onPermissionDisabled** : will be called upon some permissions are denied and "Don't show again" checkbox checked or disabled via setting

When you just want to know if a permission is granted or not, you can do :
```java
if(PermissionManager.isGranted(your_context, your_permission)){
    // Do something
}else{
    // Do other thing
}
```
If you want the permission so badly but user disabled it, you can show alert dialog to redirect user to setting :
```java
PermissionManager manager = new PermissionManager(your_activity, your_listener);
...
manager.alert("Some permission is required", "To setting", "Not now");
```
**IMPORTANT!!** your Activity must use Theme.AppCompat theme (or descendant) to call this method
## License
```
MIT License

Copyright (c) 2018 Bey Aryo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
