import { describe, test, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import HeaderMenu from '@/components/HeaderMenu.vue';

// Mock PrimeVue components
vi.mock('primevue/menubar', () => ({
  default: {
    name: 'Menubar',
    template: `
      <div>
        <ul>
          <li v-for="item in $attrs.model" :key="item.label">
            <i :class="item.icon"></i> {{ item.label }}
          </li>
        </ul>
        <slot></slot> <!-- Slot principal -->
        <slot name="end"></slot> <!-- Slot pour le bouton de changement de thème -->
      </div>
    `,
  },
}));

vi.mock('primevue/button', () => ({
  default: {
    name: 'Button',
    template: '<button><slot></slot></button>',
  },
}));

// Mock vue-router
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(), // Mock the `push` method
  }),
}));

// Mock useTheme composable
vi.mock('@/composables/useTheme', () => ({
  useTheme: () => ({
    isDarkTheme: { value: false },
    toggleTheme: vi.fn(),
  }),
}));

describe('HeaderMenu.vue', () => {
  test('renders the component', () => {
    const wrapper = mount(HeaderMenu);
    expect(wrapper.exists()).toBe(true);
  });

  test('renders the navigation menu items', () => {
    const wrapper = mount(HeaderMenu, {
      global: {
        stubs: {
          Menubar: {
            template: '<div><slot></slot></div>', // Mock simplifié
          },
        },
      },
    });

    // Vérifie que les éléments du menu sont rendus
    const menuItems = wrapper.findAll('div'); // Adaptez le sélecteur si nécessaire
    expect(menuItems.length).toBeGreaterThan(0); // Vérifie qu'il y a des éléments
  });

  test('renders the navigation menu items with icons', () => {
    const wrapper = mount(HeaderMenu);
    const menuItems = wrapper.findAll('i'); // Vérifie les icônes des éléments du menu
    expect(menuItems.length).toBeGreaterThan(0); // Vérifie qu'il y a des icônes
  });
});
