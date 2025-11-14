const form = document.getElementById('loginForm');
const msg = document.getElementById('msg');
form?.addEventListener('submit', async (e) => {
  e.preventDefault();
  msg.textContent=''; msg.className='msg';
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  try {
    const res = await fetch('/auth/login', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ email, password })
    });
    if (!res.ok) {
      const err = await res.json().catch(()=>({}));
      throw new Error(err.message || 'Credenciales inválidas');
    }
    const data = await res.json();
    localStorage.setItem('accessToken', data.accessToken);
    msg.textContent = 'Login correcto. Redirigiendo...';
    msg.classList.add('ok');
    setTimeout(() => { window.location.href = '/index'; }, 800);
  } catch (ex) {
    msg.textContent = ex.message;
    msg.classList.add('err');
  }
});
