/*---------- tag-picker (no Thymeleaf markers needed) ---------------*/
(() => {
    const MAX   = 5;
    const disp  = document.getElementById('tagSelectDisplay');
    const dd    = document.getElementById('tagDropdown');
    const hidden= document.getElementById('tagsHidden');
    const search= document.getElementById('tagSearch');
    const opts  = Array.from(dd.querySelectorAll('input[type="checkbox"]'));
    const place = document.getElementById('placeholder');
    let   selected = [];

    /* -------- helpers ---------- */
    const updateChips = () => {
        disp.querySelectorAll('.tag-chip').forEach(c => c.remove());
        selected.forEach(tag => {
            const chip = document.createElement('span');
            chip.className = 'tag-chip';
            chip.textContent = tag;

            const x = document.createElement('span');
            x.className = 'remove';
            x.textContent = '×';
            x.onclick = () => toggleTag(tag);
            chip.appendChild(x);
            disp.appendChild(chip);
        });
        place.style.display = selected.length ? 'none' : 'inline';
        hidden.value        = selected.join(',');
    };

    const toggleTag = (tag) => {
        if (selected.includes(tag)){
            selected = selected.filter(t => t !== tag);
        } else {
            if (selected.length === MAX) return;
            selected.push(tag);
        }
        const cb = opts.find(o => o.value === tag);
        if (cb) cb.checked = selected.includes(tag);
        updateChips();
    };

    /* -------- events ----------- */
    disp.addEventListener('click', () => dd.classList.toggle('hidden'));
    disp.addEventListener('keydown', e => { if (e.key === 'Enter') dd.classList.toggle('hidden'); });

    document.addEventListener('click', e => {
        if (!disp.contains(e.target) && !dd.contains(e.target)) dd.classList.add('hidden');
    });

    opts.forEach(cb => cb.addEventListener('change', () => toggleTag(cb.value)));

    search.addEventListener('input', () => {
        const q = search.value.toLowerCase();
        opts.forEach(cb => {
            cb.parentElement.style.display = cb.value.toLowerCase().includes(q) ? 'flex' : 'none';
        });
    });

    /* -------- initial-selection (edit mode) -------- */
    const initialCsv = disp.dataset.initial || '';      // read from data-attr
    if (initialCsv.trim()) {
        initialCsv.split(',').forEach(t => toggleTag(t.trim()));
    }
})();
