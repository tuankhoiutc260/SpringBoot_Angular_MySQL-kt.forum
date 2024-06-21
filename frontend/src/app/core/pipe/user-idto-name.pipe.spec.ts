import { UserIDToNamePipe } from './user-idto-name.pipe';

describe('UserIDToNamePipe', () => {
  it('create an instance', () => {
    const pipe = new UserIDToNamePipe();
    expect(pipe).toBeTruthy();
  });
});
