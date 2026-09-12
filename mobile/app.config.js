export default {
  expo: {
    owner: 'hfzkhans-team',
    name: 'BKR Fast Food Monitor',
    slug: 'hfz-khan',
    version: '1.0.0',
    android: { package: 'com.bkr.fastfood.monitor' },
    ios: { bundleIdentifier: 'com.bkr.fastfood.monitor' },
    extra: {
      apiUrl: process.env.EXPO_PUBLIC_API_URL || 'https://bkr-fastfood-backend-1zbb.onrender.com/api',
      eas: { projectId: 'c3870fd5-8594-47f1-8265-0373e8e7f408' }
    }
  }
};
