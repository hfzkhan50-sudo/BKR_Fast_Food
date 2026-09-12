import React, { useEffect, useState } from 'react';
import { ActivityIndicator, FlatList, Platform, Pressable, RefreshControl, SafeAreaView, StatusBar, StyleSheet, Text, View } from 'react-native';
import * as Notifications from 'expo-notifications';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

Notifications.setNotificationHandler({
  handleNotification: async () => ({ shouldShowAlert: true, shouldPlaySound: true, shouldSetBadge: true })
});

const API_URL = Constants.expoConfig?.extra?.apiUrl || 'https://bkr-fastfood-backend-1zbb.onrender.com/api';
const POLL_MS = 30000;

const formatMoney = (value) => `Rs ${Number(value || 0).toLocaleString('en-PK', { maximumFractionDigits: 0 })}`;
const today = () => new Date().toISOString().slice(0, 10);

async function getJson(path) {
  const response = await fetch(`${API_URL}${path}`);
  if (!response.ok) throw new Error(`Request failed (${response.status})`);
  return response.json();
}

async function notifyNewOrders(orders) {
  if (!orders.length) return;
  const previous = await AsyncStorage.getItem('lastOrderId');
  const newest = String(Math.max(...orders.map((order) => Number(order.orderId || 0))));
  if (previous && Number(newest) > Number(previous)) {
    const count = orders.filter((order) => Number(order.orderId) > Number(previous)).length;
    await Notifications.scheduleNotificationAsync({
      content: { title: 'New BKR order', body: `${count} new order${count === 1 ? '' : 's'} received.` },
      trigger: null
    });
  }
  if (!previous || Number(newest) > Number(previous)) await AsyncStorage.setItem('lastOrderId', newest);
}

export default function App() {
  const [data, setData] = useState({ sales: 0, count: 0, orders: [] });
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const loadDashboard = async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true);
    try {
      const date = today();
      const [sales, count, orders] = await Promise.all([
        getJson(`/orders/sales?date=${date}`),
        getJson(`/orders/count?date=${date}`),
        getJson(`/orders?date=${date}`)
      ]);
      await notifyNewOrders(orders);
      setData({ sales: sales.totalSales, count: count.orderCount, orders });
      setError('');
    } catch (requestError) {
      setError(requestError.message || 'Unable to reach the backend');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    if (Platform.OS !== 'web') {
      Notifications.requestPermissionsAsync();
      if (Platform.OS === 'android') Notifications.setNotificationChannelAsync('orders', { name: 'Orders', importance: Notifications.AndroidImportance.HIGH });
    }
    loadDashboard();
    const timer = setInterval(() => loadDashboard(), POLL_MS);
    return () => clearInterval(timer);
  }, []);

  if (loading) return <View style={styles.loading}><ActivityIndicator size="large" color="#e85d35" /><Text style={styles.loadingText}>Connecting to BKR Fast Food...</Text></View>;

  return (
    <SafeAreaView style={styles.safe}>
      <StatusBar barStyle="dark-content" />
      <FlatList
        contentContainerStyle={styles.content}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => loadDashboard(true)} tintColor="#e85d35" />}
        ListHeaderComponent={(
          <>
            <View style={styles.header}>
              <View><Text style={styles.eyebrow}>TODAY'S CONTROL ROOM</Text><Text style={styles.title}>BKR Fast Food</Text></View>
              <View style={styles.live}><View style={styles.liveDot} /><Text style={styles.liveText}>LIVE</Text></View>
            </View>
            <Text style={styles.date}>{new Date().toLocaleDateString('en-PK', { weekday: 'long', day: 'numeric', month: 'long' })}</Text>
            {error ? <Pressable onPress={() => loadDashboard(true)} style={styles.error}><Text style={styles.errorTitle}>Connection issue</Text><Text style={styles.errorText}>{error}. Tap to retry.</Text></Pressable> : null}
            <View style={styles.metrics}>
              <View style={[styles.metric, styles.salesMetric]}><Text style={styles.metricLabel}>TOTAL SALES</Text><Text style={styles.salesValue}>{formatMoney(data.sales)}</Text><Text style={styles.metricHint}>Today</Text></View>
              <View style={[styles.metric, styles.ordersMetric]}><Text style={styles.metricLabel}>TOTAL ORDERS</Text><Text style={styles.ordersValue}>{data.count}</Text><Text style={styles.metricHint}>Today</Text></View>
            </View>
            <View style={styles.sectionHeader}><Text style={styles.sectionTitle}>Recent orders</Text><Text style={styles.sectionCount}>{data.orders.length} today</Text></View>
          </>
        )}
        data={data.orders}
        keyExtractor={(item) => String(item.orderId)}
        renderItem={({ item }) => (
          <View style={styles.orderRow}><View style={styles.orderBadge}><Text style={styles.orderBadgeText}>#{item.orderId}</Text></View><View style={styles.orderInfo}><Text style={styles.orderNo}>{item.orderNo || 'Order'}</Text><Text style={styles.orderMeta}>{item.customerName || 'Walk-in'}  ·  {item.orderType || 'Dine In'}</Text></View><Text style={styles.orderTotal}>{formatMoney(item.totalAmount)}</Text></View>
        )}
        ListEmptyComponent={<View style={styles.empty}><Text style={styles.emptyTitle}>No orders yet</Text><Text style={styles.emptyText}>New orders will appear here automatically.</Text></View>}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#f7f4ef' },
  content: { padding: 22, paddingBottom: 36 },
  loading: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: '#f7f4ef' },
  loadingText: { marginTop: 14, color: '#716b63', fontSize: 15 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', marginTop: 12 },
  eyebrow: { color: '#e85d35', fontSize: 11, fontWeight: '800', letterSpacing: 1.4 },
  title: { color: '#25221f', fontSize: 30, fontWeight: '800', marginTop: 6 },
  date: { color: '#827a71', fontSize: 14, marginTop: 8 },
  live: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', paddingHorizontal: 10, paddingVertical: 7, borderRadius: 20 },
  liveDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: '#39a96b', marginRight: 6 },
  liveText: { color: '#318957', fontSize: 11, fontWeight: '800' },
  metrics: { flexDirection: 'row', gap: 12, marginTop: 24 },
  metric: { flex: 1, minHeight: 132, borderRadius: 18, padding: 18, justifyContent: 'space-between' },
  salesMetric: { backgroundColor: '#e85d35' },
  ordersMetric: { backgroundColor: '#292522' },
  metricLabel: { color: '#fff', opacity: 0.78, fontSize: 11, fontWeight: '800', letterSpacing: 0.8 },
  salesValue: { color: '#fff', fontSize: 23, fontWeight: '800' },
  ordersValue: { color: '#fff', fontSize: 36, fontWeight: '800' },
  metricHint: { color: '#fff', opacity: 0.7, fontSize: 12 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 30, marginBottom: 12 },
  sectionTitle: { color: '#25221f', fontSize: 20, fontWeight: '800' },
  sectionCount: { color: '#978e84', fontSize: 13 },
  orderRow: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 14, padding: 13, marginBottom: 9 },
  orderBadge: { width: 42, height: 42, borderRadius: 12, backgroundColor: '#f8e4dc', alignItems: 'center', justifyContent: 'center' },
  orderBadgeText: { color: '#d84e2c', fontWeight: '800', fontSize: 12 },
  orderInfo: { flex: 1, marginLeft: 12 },
  orderNo: { color: '#302b27', fontSize: 15, fontWeight: '800' },
  orderMeta: { color: '#978e84', fontSize: 12, marginTop: 4 },
  orderTotal: { color: '#302b27', fontSize: 14, fontWeight: '800' },
  empty: { alignItems: 'center', padding: 28, backgroundColor: '#fff', borderRadius: 14 },
  emptyTitle: { color: '#302b27', fontSize: 16, fontWeight: '800' },
  emptyText: { color: '#978e84', marginTop: 6, fontSize: 13 },
  error: { backgroundColor: '#fff0ed', borderRadius: 14, padding: 13, marginTop: 18 },
  errorTitle: { color: '#b93d24', fontWeight: '800' },
  errorText: { color: '#b96551', marginTop: 4, fontSize: 13 }
});