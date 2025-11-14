const form = document.getElementById('signupForm');
const msg = document.getElementById('msg');
form?.addEventListener('submit', async (e) => {
  e.preventDefault();
  msg.textContent=''; msg.className='msg';
  const payload = {
    nombre: document.getElementById('nombre').value.trim(),
    email: document.getElementById('email').value.trim(),
    password: document.getElementById('password').value
  };
  try {
    const res = await fetch('/auth/signup', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify(payload)
    });
    if (!res.ok) {
      const err = await res.json().catch(()=>({}));
      throw new Error(err.message || 'No se pudo registrar');
    }
    msg.textContent = 'Cuenta creada. Redirigiendo al login...';
    msg.classList.add('ok');
    setTimeout(() => { window.location.href = '/login'; }, 800);
  } catch (ex) {
    msg.textContent = ex.message;
    msg.classList.add('err');
  }
});
