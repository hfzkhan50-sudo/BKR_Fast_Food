export default {
  expo: {
    name: 'BKR Fast Food Monitor',
    slug: 'bkr-fast-food-monitor',
    version: '1.0.0',
    android: { package: 'com.bkr.fastfood.monitor' },
    ios: { bundleIdentifier: 'com.bkr.fastfood.monitor' },
    extra: {
      apiUrl: process.env.EXPO_PUBLIC_API_URL || 'https://bkr-fastfood-backend-1zbb.onrender.com/api'
    }
  }
};
