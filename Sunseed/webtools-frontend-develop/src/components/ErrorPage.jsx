import styled from 'styled-components';
// import NoNetwork from '../assets/NoNetwork.png';

function ErrorPage({ error }) {
  const darkMode = localStorage.getItem('darkMode');
  return (
    <Main style={{ backgroundColor: darkMode ? '#454D56' : '#f9fafb' }}>
      {/* <img src={NoNetwork} alt='Something went wrong'></img> */}

      <p className='oops' style={{ color: darkMode ? '#FFFFFF' : '#434341' }}>
        Oops! Something went wrong
      </p>
      {/* <pre style={{ color: "red" }}>{"Message: " + error.message}</pre> */}
      <p className='message' style={{ color: darkMode ? '#FFFFFF' : '#434341' }}>
        Brace yourself till we get the error fixed.
      </p>
      <p className='message' style={{ color: darkMode ? '#FFFFFF' : '#434341' }}>
        You may also refresh the page or try again later
      </p>
    </Main>
  );
}

const Main = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  color: #434141;
  font-family: 'Open Sans';
  font-style: normal;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    height: 250px;
  }

  .oops {
    font-weight: 600;
    font-size: 28px;
    line-height: 38px;
    margin: 20px 0px;
  }
  .message {
    font-weight: 400;
    font-size: 16px;
    line-height: 22px;
  }
`;

export default ErrorPage;
