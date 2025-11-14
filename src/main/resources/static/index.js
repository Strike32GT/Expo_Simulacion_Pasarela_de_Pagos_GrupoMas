// Guard de token
(function(){
  const token = localStorage.getItem('accessToken');
  if (!token) {
    window.location.href = '/login';
  }
  document.getElementById('logoutBtn')?.addEventListener('click', () => {
    localStorage.removeItem('accessToken');
  });
})();

// Tabs simples
const tabs = document.querySelectorAll('.tab');
const panels = document.querySelectorAll('.tab-panel');
tabs.forEach(t => t.addEventListener('click', ()=>{
  tabs.forEach(x=>x.classList.remove('active'));
  panels.forEach(x=>x.classList.remove('active'));
  t.classList.add('active');
  document.getElementById(`tab-${t.dataset.tab}`).classList.add('active');
}));

// Handlers
const logEl = document.getElementById('log');
const setLog = (obj) => { if(logEl) logEl.textContent = JSON.stringify(obj, null, 2); };

// Cargar usuarios para selects de origen/destino
async function loadUsers() {
  const token = localStorage.getItem('accessToken');
  if (!token) return;
  const originSel = document.getElementById('originSelect');
  const destSel = document.getElementById('destinoSelect');
  if (!originSel || !destSel) return;
  try {
    const res = await fetch('/usuarios', { headers: { Authorization: `Bearer ${token}` } });
    const users = await res.json();
    originSel.innerHTML = '';
    destSel.innerHTML = '';
    users.forEach(u => {
      const opt1 = document.createElement('option');
      opt1.value = u.id;
      opt1.textContent = `${u.nombre} (${u.email}) - ${u.rol}`;
      originSel.appendChild(opt1);
      const opt2 = document.createElement('option');
      opt2.value = u.id;
      opt2.textContent = `${u.nombre} (${u.email}) - ${u.rol}`;
      destSel.appendChild(opt2);
    });
    // Si hay al menos 2 usuarios, evitar que origen y destino sean iguales por defecto
    if (originSel.options.length > 1) {
      destSel.selectedIndex = 1;
    }
  } catch (e) {
    setLog({ error: 'No se pudo cargar usuarios', message: e.message });
  }
}
loadUsers();

document.getElementById('cardForm')?.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const token = localStorage.getItem('accessToken');
  if (!token) { window.location.href = '/login'; return; }
  const monto = parseFloat(document.getElementById('cardAmount')?.value || '0');
  if (!monto || monto <= 0) { setLog({ error:'Monto inválido' }); return; }
  // Validaciones de tarjeta
  const numberEl = document.getElementById('cardNumber');
  const holderEl = document.getElementById('cardHolder');
  const expEl = document.getElementById('cardExp');
  const cvvEl = document.getElementById('cardCvv');

  const numDigits = (numberEl?.value || '').replace(/\D/g,'');
  if (numDigits.length !== 16) { setLog({ error:'El número de tarjeta debe tener 16 dígitos' }); return; }

  const exp = (expEl?.value || '').trim();
  if (!/^\d{2}\/\d{2}$/.test(exp)) { setLog({ error:'Formato de vencimiento inválido (MM/AA)' }); return; }
  const mm = parseInt(exp.slice(0,2),10);
  if (mm < 1 || mm > 12) { setLog({ error:'Mes de vencimiento inválido' }); return; }

  const cvv = (cvvEl?.value || '').replace(/\D/g,'');
  if (!(cvv.length === 3 || cvv.length === 4)) { setLog({ error:'CVV debe tener 3 o 4 dígitos' }); return; }

  const holder = (holderEl?.value || '').trim();
  if (!holder || /[^A-Za-zÁÉÍÓÚáéíóúÑñ ]/.test(holder)) { setLog({ error:'El nombre del titular solo debe contener letras y espacios' }); return; }
  const originSel = document.getElementById('originSelect');
  const destSel = document.getElementById('destinoSelect');
  const origenId = originSel ? parseInt(originSel.value, 10) : null;
  const destinoId = destSel ? parseInt(destSel.value, 10) : null;
  if (origenId && destinoId && origenId === destinoId) { setLog({ error:'Origen y destino no pueden ser iguales' }); return; }
  const body = { monto, metodo:'TARJETA', origenId, destinoId };
  try {
    const res = await fetch('/pagos', {
      method:'POST',
      headers:{ 'Content-Type':'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(body)
    });
    const data = await res.json().catch(()=>({}));
    if (!res.ok) throw new Error(data.message || 'Error al crear el pago');
    setLog(data);
  } catch (err) {
    setLog({ ok:false, message: err.message });
  }
});
// Filtro para teléfono Yape: solo dígitos, máx 9
const yapePhoneInput = document.getElementById('yapePhone');
yapePhoneInput?.addEventListener('input', () => {
  yapePhoneInput.value = yapePhoneInput.value.replace(/\D/g, '').slice(0,9);
});

// Envío Yape con validaciones y POST al backend
document.getElementById('yapeForm')?.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const token = localStorage.getItem('accessToken');
  if (!token) { window.location.href = '/login'; return; }
  const phone = (document.getElementById('yapePhone')?.value || '').replace(/\D/g,'');
  if (!/^9\d{8}$/.test(phone)) { setLog({ error:'Número Yape inválido. Debe iniciar con 9 y tener 9 dígitos.' }); return; }
  const monto = parseFloat(document.getElementById('yapeAmount')?.value || '0');
  if (!monto || monto <= 0) { setLog({ error:'Monto inválido' }); return; }
  // Reutilizamos selects globales de origen/destino
  const originSel = document.getElementById('originSelect');
  const destSel = document.getElementById('destinoSelect');
  const origenId = originSel ? parseInt(originSel.value, 10) : null;
  const destinoId = destSel ? parseInt(destSel.value, 10) : null;
  if (origenId && destinoId && origenId === destinoId) { setLog({ error:'Origen y destino no pueden ser iguales' }); return; }
  const body = { monto, metodo:'YAPE', origenId, destinoId };
  try {
    const res = await fetch('/pagos', {
      method:'POST',
      headers:{ 'Content-Type':'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(body)
    });
    const data = await res.json().catch(()=>({}));
    if (!res.ok) throw new Error(data.message || 'Error al crear el pago Yape');
    setLog(data);
  } catch (err) {
    setLog({ ok:false, message: err.message });
  }
});
document.getElementById('paypalForm')?.addEventListener('submit', async (e)=>{
  e.preventDefault();
  const token = localStorage.getItem('accessToken');
  if (!token) { window.location.href = '/login'; return; }
  const email = (document.getElementById('paypalEmail')?.value || '').trim();
  // Validación simple de email
  const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  if (!emailOk) { setLog({ error:'Email de PayPal inválido' }); return; }
  const monto = parseFloat(document.getElementById('paypalAmount')?.value || '0');
  if (!monto || monto <= 0) { setLog({ error:'Monto inválido' }); return; }
  const originSel = document.getElementById('originSelect');
  const destSel = document.getElementById('destinoSelect');
  const origenId = originSel ? parseInt(originSel.value, 10) : null;
  const destinoId = destSel ? parseInt(destSel.value, 10) : null;
  if (origenId && destinoId && origenId === destinoId) { setLog({ error:'Origen y destino no pueden ser iguales' }); return; }
  const body = { monto, metodo:'PAYPAL', origenId, destinoId };
  try {
    const res = await fetch('/pagos', {
      method:'POST',
      headers:{ 'Content-Type':'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(body)
    });
    const data = await res.json().catch(()=>({}));
    if (!res.ok) throw new Error(data.message || 'Error al crear el pago PayPal');
    setLog(data);
  } catch (err) {
    setLog({ ok:false, message: err.message });
  }
});

// Detección de marca y mini tarjeta
const cardInput = document.getElementById('cardNumber');
const miniCard = document.getElementById('miniCard');
const miniBrand = document.getElementById('miniCardBrand');
const miniNumber = document.getElementById('miniCardNumber');

const detectBrand = (digits) => {
  if (digits.startsWith('4')) return 'VISA';
  if (/^(5)/.test(digits)) return 'MASTERCARD';
  if (/^(34|37)/.test(digits)) return 'AMEX';
  if (/^(36|38|30)/.test(digits)) return 'DINERS';
  return 'UNKNOWN';
};

const brandClass = (brand) => ({
  VISA: 'card-visa',
  MASTERCARD: 'card-master',
  AMEX: 'card-amex',
  DINERS: 'card-diners',
  UNKNOWN: 'card-black'
}[brand] || 'card-black');

const formatNumber = (val) => val.replace(/\D/g,'')
  .replace(/(.{4})/g,'$1 ')
  .trim();

cardInput?.addEventListener('input', () => {
  let raw = cardInput.value.replace(/\D/g,'');
  // limitar a 16 dígitos (estándar)
  raw = raw.slice(0,16);
  const brand = detectBrand(raw);
  // máscara simple
  const shown = formatNumber(raw.padEnd(16,'•'));
  if (miniNumber) miniNumber.textContent = shown || '•••• •••• •••• ••••';
  if (miniBrand) miniBrand.textContent = brand === 'UNKNOWN' ? 'Tarjeta' : brand;
  if (miniCard) {
    miniCard.classList.remove('card-black','card-visa','card-master','card-diners','card-amex');
    miniCard.classList.add(brandClass(brand));
  }
  // reflejar valor formateado en el input
  cardInput.value = formatNumber(raw);
});

// Autoformato de vencimiento MM/AA y validación básica
const expInput = document.getElementById('cardExp');
expInput?.addEventListener('input', () => {
  let digits = expInput.value.replace(/\D/g, '').slice(0,4); // MMYY
  if (digits.length >= 3) {
    expInput.value = `${digits.slice(0,2)}/${digits.slice(2)}`;
  } else if (digits.length >= 1) {
    expInput.value = digits;
  } else {
    expInput.value = '';
  }
});

// CVV solo dígitos, 3 o 4
const cvvInput = document.getElementById('cardCvv');
cvvInput?.addEventListener('input', () => {
  cvvInput.value = cvvInput.value.replace(/\D/g, '').slice(0,4);
});

// Titular solo letras y espacios
const holderInput = document.getElementById('cardHolder');
holderInput?.addEventListener('input', () => {
  holderInput.value = holderInput.value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ ]+/g, '').replace(/\s{2,}/g,' ');
});
