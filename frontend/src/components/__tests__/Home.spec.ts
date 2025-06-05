import { describe, test, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import Home from '@/pages/Home.vue';

describe('Home.vue', () => {
  test('renders the main title', () => {
    const wrapper = mount(Home);
    expect(wrapper.text()).toContain('Statistiques du COVID et SARS');
  });

  test('renders the Map3D component', () => {
    const wrapper = mount(Home);
    expect(wrapper.findComponent({ name: 'Map3D' }).exists()).toBe(true);
  });
});
