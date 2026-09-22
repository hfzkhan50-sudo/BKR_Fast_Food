import React, { useEffect, useState } from 'react';
import { ActivityIndicator, FlatList, Modal, Platform, Pressable, RefreshControl, SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, View } from 'react-native';
import * as Notifications from 'expo-notifications';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

Notifications.setNotificationHandler({
  handleNotification: async () => ({ shouldShowAlert: true, shouldPlaySound: true, shouldSetBadge: true })
});

const API_URL = Constants.expoConfig?.extra?.apiUrl || 'https://bkr-fastfood-backend-1zbb.onrender.com/api';
const POLL_MS = 30000;

const GOLD = '#e8b84b';
const GOLD_BRIGHT = '#f6ce6a';
const RED = '#a52322';
const RED_DARK = '#7a1918';
const BLACK = '#0e0c0b';
const CARD = '#1c1815';
const CREAM = '#f3ead9';

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

/** Pure RN approximation of the BKR badge: gold ring, red dome, arched wordmark. */
function BkrLogoBadge({ size = 64 }) {
  const ring = size;
  const dome = size - Math.max(4, Math.round(size * 0.09));
  return (
    <View style={[styles.logoRing, { width: ring, height: ring, borderRadius: ring / 2 }]}>
      <View style={[styles.logoDome, { width: dome, height: dome, borderRadius: dome / 2 }]}>
        <Text style={[styles.logoBkr, { fontSize: size * 0.34 }]}>BKR</Text>
        <View style={styles.logoBanner}>
          <Text style={styles.logoBannerText}>BACHA KHAN</Text>
        </View>
      </View>
    </View>
  );
}

function OrderDetailModal({ visible, onClose, loading, error, order }) {
  return (
    <Modal visible={visible} animationType="slide" transparent onRequestClose={onClose}>
      <View style={styles.modalOverlay}>
        <View style={styles.modalCard}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>{order ? (order.orderNo || `Order #${order.orderId}`) : 'Order Details'}</Text>
            <Pressable onPress={onClose} hitSlop={10}><Text style={styles.modalClose}>Close</Text></Pressable>
          </View>
          {loading ? (
            <ActivityIndicator size="large" color={GOLD} style={{ marginVertical: 30 }} />
          ) : error ? (
            <Text style={styles.errorText}>{error}</Text>
          ) : order ? (
            <ScrollView style={{ maxHeight: 420 }}>
              <Text style={styles.modalMeta}>{order.customerName || 'Walk-in'}  ·  {order.orderType || 'Dine In'}  ·  {order.paymentType || 'Cash'}</Text>
              <Text style={styles.modalMeta}>{order.orderDate || ''} {order.orderTime || ''}</Text>
              <View style={styles.modalDivider} />
              {(order.items || []).map((line, index) => (
                <View key={index} style={styles.lineRow}>
                  <View style={{ flex: 1 }}>
                    <Text style={styles.lineName}>{line.menuItemName}</Text>
                    <Text style={styles.lineQty}>Qty {line.quantity} × {formatMoney(line.unitPrice)}</Text>
                  </View>
                  <Text style={styles.lineTotal}>{formatMoney(line.lineTotal)}</Text>
                </View>
              ))}
              <View style={styles.modalDivider} />
              <View style={styles.totalsRow}><Text style={styles.totalsLabel}>Subtotal</Text><Text style={styles.totalsValue}>{formatMoney(order.subtotal)}</Text></View>
              {Number(order.discount) > 0 ? <View style={styles.totalsRow}><Text style={styles.totalsLabel}>Discount</Text><Text style={styles.totalsValue}>-{formatMoney(order.discount)}</Text></View> : null}
              {Number(order.deliveryCharge) > 0 ? <View style={styles.totalsRow}><Text style={styles.totalsLabel}>Delivery</Text><Text style={styles.totalsValue}>{formatMoney(order.deliveryCharge)}</Text></View> : null}
              {Number(order.serviceCharge) > 0 ? <View style={styles.totalsRow}><Text style={styles.totalsLabel}>Service ({order.serviceChargePercent}%)</Text><Text style={styles.totalsValue}>{formatMoney(order.serviceCharge)}</Text></View> : null}
              <View style={styles.totalsRow}><Text style={styles.grandLabel}>Total</Text><Text style={styles.grandValue}>{formatMoney(order.totalAmount)}</Text></View>
            </ScrollView>
          ) : null}
        </View>
      </View>
    </Modal>
  );
}

export default function App() {
  const [data, setData] = useState({ sales: 0, count: 0, orders: [], inventory: [], cancelledSales: 0, cancelledOrders: [] });
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  const [detailVisible, setDetailVisible] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState('');
  const [detailOrder, setDetailOrder] = useState(null);

  const loadDashboard = async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true);
    try {
      const date = today();
      const [sales, count, orders, inventory, cancelledSales, cancelledOrders] = await Promise.all([
        getJson(`/orders/sales?date=${date}`),
        getJson(`/orders/count?date=${date}`),
        getJson(`/orders?date=${date}`),
        getJson('/inventory'),
        getJson(`/orders/cancelled/sales?date=${date}`),
        getJson(`/orders/cancelled/range?from=${date}&to=${date}`)
      ]);
      await notifyNewOrders(orders);
      setData({ sales: sales.totalSales, count: count.orderCount, orders, inventory, cancelledSales: cancelledSales.totalSales, cancelledOrders });
      setError('');
    } catch (requestError) {
      setError(requestError.message || 'Unable to reach the backend');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const openOrderDetail = async (orderId) => {
    setDetailVisible(true);
    setDetailLoading(true);
    setDetailError('');
    setDetailOrder(null);
    try {
      const order = await getJson(`/orders/${orderId}`);
      setDetailOrder(order);
    } catch (requestError) {
      setDetailError(requestError.message || 'Unable to load order details');
    } finally {
      setDetailLoading(false);
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

  if (loading) return <View style={styles.loading}><ActivityIndicator size="large" color={GOLD} /><Text style={styles.loadingText}>Connecting to BKR Fast Food...</Text></View>;

  return (
    <SafeAreaView style={styles.safe}>
      <StatusBar barStyle="light-content" backgroundColor={BLACK} />
      <FlatList
        contentContainerStyle={styles.content}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => loadDashboard(true)} tintColor={GOLD} />}
        ListHeaderComponent={(
          <>
            <View style={styles.header}>
              <BkrLogoBadge size={58} />
              <View style={styles.headerTitleBox}>
                <Text style={styles.title}>BKR BACHA KHAN</Text>
                <Text style={styles.subtitle}>AND FAST FOOD</Text>
              </View>
              <View style={styles.live}><View style={styles.liveDot} /><Text style={styles.liveText}>LIVE</Text></View>
            </View>
            <Text style={styles.date}>{new Date().toLocaleDateString('en-PK', { weekday: 'long', day: 'numeric', month: 'long' })}</Text>
            {error ? <Pressable onPress={() => loadDashboard(true)} style={styles.error}><Text style={styles.errorTitle}>Connection issue</Text><Text style={styles.errorText}>{error}. Tap to retry.</Text></Pressable> : null}
            <View style={styles.metrics}>
              <View style={[styles.metric, styles.salesMetric]}><Text style={styles.metricLabel}>TOTAL SALES</Text><Text style={styles.salesValue}>{formatMoney(data.sales)}</Text><Text style={styles.metricHint}>Today</Text></View>
              <View style={[styles.metric, styles.ordersMetric]}><Text style={styles.metricLabel}>TOTAL ORDERS</Text><Text style={styles.ordersValue}>{data.count}</Text><Text style={styles.metricHint}>Today</Text></View>
            </View>
            <View style={styles.sectionHeader}><Text style={styles.sectionTitle}>Recent orders</Text><Text style={styles.sectionCount}>{data.orders.length} today  ·  tap for details</Text></View>
          </>
        )}
        data={data.orders}
        keyExtractor={(item) => String(item.orderId)}
        renderItem={({ item }) => (
          <Pressable style={({ pressed }) => [styles.orderRow, pressed && styles.orderRowPressed]} onPress={() => openOrderDetail(item.orderId)}>
            <View style={styles.orderBadge}><Text style={styles.orderBadgeText}>#{item.orderId}</Text></View>
            <View style={styles.orderInfo}><Text style={styles.orderNo}>{item.orderNo || 'Order'}</Text><Text style={styles.orderMeta}>{item.customerName || 'Walk-in'}  ·  {item.orderType || 'Dine In'}</Text></View>
            <Text style={styles.orderTotal}>{formatMoney(item.totalAmount)}</Text>
          </Pressable>
        )}
        ListEmptyComponent={<View style={styles.empty}><Text style={styles.emptyTitle}>No orders yet</Text><Text style={styles.emptyText}>New orders will appear here automatically.</Text></View>}
        ListFooterComponent={(
          <>
            <View style={styles.sectionHeader}><Text style={styles.sectionTitle}>Inventory</Text><Text style={styles.sectionCount}>{data.inventory.length} items</Text></View>
            <View style={styles.inventoryCard}>
              {data.inventory.map((item) => (
                <View key={String(item.itemId)} style={styles.inventoryRow}>
                  <Text style={styles.inventoryName}>{item.itemName}</Text>
                  <Text style={[styles.inventoryQuantity, Number(item.quantity) <= Number(item.reorderLevel) && styles.lowStock]}>{Number(item.quantity).toLocaleString('en-PK')} available</Text>
                </View>
              ))}
              {!data.inventory.length ? <Text style={styles.emptyText}>No inventory items.</Text> : null}
            </View>
            <View style={styles.sectionHeader}><Text style={styles.sectionTitle}>Cancelled orders</Text><Text style={styles.sectionCount}>{data.cancelledOrders.length} today</Text></View>
            <View style={styles.cancelledSummary}><Text style={styles.cancelledLabel}>Cancelled total</Text><Text style={styles.cancelledValue}>{formatMoney(data.cancelledSales)}</Text></View>
            {data.cancelledOrders.map((item) => (
              <View key={String(item.orderId)} style={styles.cancelledRow}>
                <Text style={styles.cancelledOrderNo}>{item.orderNo || `Order #${item.orderId}`}</Text>
                <Text style={styles.cancelledOrderTotal}>{formatMoney(item.totalAmount)}</Text>
              </View>
            ))}
          </>
        )}
      />
      <OrderDetailModal
        visible={detailVisible}
        onClose={() => setDetailVisible(false)}
        loading={detailLoading}
        error={detailError}
        order={detailOrder}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: BLACK },
  content: { padding: 22, paddingBottom: 36 },
  loading: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: BLACK },
  loadingText: { marginTop: 14, color: CREAM, fontSize: 15 },

  logoRing: { backgroundColor: GOLD, alignItems: 'center', justifyContent: 'center', borderWidth: 2, borderColor: GOLD_BRIGHT },
  logoDome: { backgroundColor: RED_DARK, alignItems: 'center', justifyContent: 'center', borderWidth: 1, borderColor: GOLD_BRIGHT, overflow: 'hidden' },
  logoBkr: { color: GOLD_BRIGHT, fontWeight: '800', letterSpacing: 1 },
  logoBanner: { position: 'absolute', bottom: 6, backgroundColor: RED, paddingHorizontal: 4, borderRadius: 2 },
  logoBannerText: { color: '#fff', fontSize: 6, fontWeight: '800' },

  header: { flexDirection: 'row', alignItems: 'center', marginTop: 12, gap: 12 },
  headerTitleBox: { flex: 1 },
  title: { color: GOLD_BRIGHT, fontSize: 19, fontWeight: '800', letterSpacing: 0.5 },
  subtitle: { color: RED, fontSize: 12, fontWeight: '800', letterSpacing: 1, marginTop: 2 },
  date: { color: '#a89e90', fontSize: 14, marginTop: 12 },
  live: { flexDirection: 'row', alignItems: 'center', backgroundColor: CARD, paddingHorizontal: 10, paddingVertical: 7, borderRadius: 20, borderWidth: 1, borderColor: '#332c26' },
  liveDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: '#39a96b', marginRight: 6 },
  liveText: { color: '#7fd8a4', fontSize: 11, fontWeight: '800' },
  metrics: { flexDirection: 'row', gap: 12, marginTop: 24 },
  metric: { flex: 1, minHeight: 132, borderRadius: 18, padding: 18, justifyContent: 'space-between', borderWidth: 1 },
  salesMetric: { backgroundColor: RED, borderColor: RED_DARK },
  ordersMetric: { backgroundColor: CARD, borderColor: '#332c26' },
  metricLabel: { color: '#fff', opacity: 0.8, fontSize: 11, fontWeight: '800', letterSpacing: 0.8 },
  salesValue: { color: '#fff', fontSize: 23, fontWeight: '800' },
  ordersValue: { color: GOLD_BRIGHT, fontSize: 36, fontWeight: '800' },
  metricHint: { color: '#fff', opacity: 0.7, fontSize: 12 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 30, marginBottom: 12 },
  sectionTitle: { color: CREAM, fontSize: 20, fontWeight: '800' },
  sectionCount: { color: '#978e84', fontSize: 13 },
  orderRow: { flexDirection: 'row', alignItems: 'center', backgroundColor: CARD, borderRadius: 14, padding: 13, marginBottom: 9, borderWidth: 1, borderColor: '#2a241f' },
  orderRowPressed: { backgroundColor: '#25201b' },
  orderBadge: { width: 42, height: 42, borderRadius: 12, backgroundColor: RED_DARK, alignItems: 'center', justifyContent: 'center' },
  orderBadgeText: { color: GOLD_BRIGHT, fontWeight: '800', fontSize: 12 },
  orderInfo: { flex: 1, marginLeft: 12 },
  orderNo: { color: CREAM, fontSize: 15, fontWeight: '800' },
  orderMeta: { color: '#978e84', fontSize: 12, marginTop: 4 },
  orderTotal: { color: GOLD_BRIGHT, fontSize: 14, fontWeight: '800' },
  empty: { alignItems: 'center', padding: 28, backgroundColor: CARD, borderRadius: 14 },
  emptyTitle: { color: CREAM, fontSize: 16, fontWeight: '800' },
  emptyText: { color: '#978e84', marginTop: 6, fontSize: 13 },
  inventoryCard: { backgroundColor: CARD, borderRadius: 14, padding: 13, borderWidth: 1, borderColor: '#2a241f' },
  inventoryRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 9, borderBottomWidth: 1, borderBottomColor: '#332c26' },
  inventoryName: { color: CREAM, fontSize: 14, fontWeight: '700', flex: 1 },
  inventoryQuantity: { color: GOLD_BRIGHT, fontSize: 13, fontWeight: '700' },
  lowStock: { color: '#ff9a86' },
  cancelledSummary: { flexDirection: 'row', justifyContent: 'space-between', backgroundColor: '#2a1613', borderRadius: 14, padding: 14, borderWidth: 1, borderColor: RED_DARK },
  cancelledLabel: { color: '#e8b6a8', fontSize: 14, fontWeight: '700' },
  cancelledValue: { color: '#ff9a86', fontSize: 16, fontWeight: '800' },
  cancelledRow: { flexDirection: 'row', justifyContent: 'space-between', backgroundColor: CARD, borderRadius: 12, padding: 13, marginTop: 8, borderWidth: 1, borderColor: '#332c26' },
  cancelledOrderNo: { color: CREAM, fontSize: 14, fontWeight: '700' },
  cancelledOrderTotal: { color: '#ff9a86', fontSize: 14, fontWeight: '800' },
  error: { backgroundColor: '#2a1613', borderRadius: 14, padding: 13, marginTop: 18, borderWidth: 1, borderColor: RED_DARK },
  errorTitle: { color: '#ff9a86', fontWeight: '800' },
  errorText: { color: '#e8b6a8', marginTop: 4, fontSize: 13 },

  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.6)', justifyContent: 'flex-end' },
  modalCard: { backgroundColor: CARD, borderTopLeftRadius: 22, borderTopRightRadius: 22, padding: 20, maxHeight: '80%' },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 },
  modalTitle: { color: GOLD_BRIGHT, fontSize: 17, fontWeight: '800' },
  modalClose: { color: '#e8b6a8', fontWeight: '700' },
  modalMeta: { color: '#a89e90', fontSize: 12, marginTop: 2 },
  modalDivider: { height: 1, backgroundColor: '#332c26', marginVertical: 12 },
  lineRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 10 },
  lineName: { color: CREAM, fontSize: 14, fontWeight: '700' },
  lineQty: { color: '#978e84', fontSize: 12, marginTop: 2 },
  lineTotal: { color: GOLD_BRIGHT, fontSize: 14, fontWeight: '800' },
  totalsRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 6 },
  totalsLabel: { color: '#a89e90', fontSize: 13 },
  totalsValue: { color: CREAM, fontSize: 13, fontWeight: '700' },
  grandLabel: { color: CREAM, fontSize: 16, fontWeight: '800', marginTop: 4 },
  grandValue: { color: GOLD_BRIGHT, fontSize: 18, fontWeight: '800', marginTop: 4 }
});